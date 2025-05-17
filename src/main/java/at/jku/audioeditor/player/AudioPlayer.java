package at.jku.audioeditor.player;

import at.jku.audioeditor.player.progress.AudioProgress;
import at.jku.audioeditor.player.progress.AudioProgressListener;
import at.jku.audioeditor.player.progress.AudioProgressLogger;
import at.jku.audioeditor.source.AudioSource;
import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.*;
import javax.sound.sampled.Line.Info;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User    : sfeiler
 * Date    : 12/05/2025
 * Project : Audio_Editor
 */

@Slf4j
public class AudioPlayer implements Runnable {
    private static final int AUDIO_BUFFER_DEFAULT_SIZE = 1024;
    private static int audioPlayerCount = 0;

    final AudioSource audioSource;
    List<AudioProgressListener> audioProgressListeners;

    Thread audioThread;
    Runnable threadInterruptionCallback;
    SourceDataLine sourceDataLine;
    AudioFormat outputAudioFormat;

    int bytesRead = 0;
    long totalBytesRead = 0;
    volatile double playBackRate = 1.0;
    //needed because SourceDataLine seems to reset Thread interrupt flag
    volatile boolean interrupted = false;
    byte[] buffer = new byte[AUDIO_BUFFER_DEFAULT_SIZE];

    public AudioPlayer(AudioSource audioSource) {
        audioPlayerCount++;
        this.audioSource = audioSource;
        try {
            init();
        } catch (LineUnavailableException e) {
            log.error("Unable to init SourceDataLine for audio writing.", e);
        }
    }

    private void init() throws LineUnavailableException {
        AudioFormat inputAudioFormat = audioSource.getAudioFormat();
        outputAudioFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                inputAudioFormat.getSampleRate(),
                inputAudioFormat.getSampleSizeInBits(),
                inputAudioFormat.getChannels(),
                inputAudioFormat.getFrameSize(),
                inputAudioFormat.getFrameRate(),
                inputAudioFormat.isBigEndian());
        Info info = new DataLine.Info(SourceDataLine.class, outputAudioFormat);
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
        audioProgressListeners = new ArrayList<>();
        audioProgressListeners.add(new AudioProgressLogger());
    }

    public void play() {
        //do nothing if thread is currently running
        if(audioThread == null || !audioThread.isAlive()) {
            audioThread = new Thread(this, "AudioPlayer" + audioSource.audioId());
            audioThread.setDaemon(true);
            audioThread.start();
        }
    }

    public void pause() {
        if(!interrupted) {
            interruptAudioPlayer();
        }
    }

    public void stop() {
        if(!interrupted) {
            threadInterruptionCallback = this::resetAudioPlayer;
            interruptAudioPlayer();
        }
    }

    public void setPlayBackRate(double rate) {
        playBackRate = rate;
        playerSettingsChanged();
    }

    private void playerSettingsChanged() {
        threadInterruptionCallback = () -> {
            //TODO: recalculate audioFormat and sourceDataLine
        };
        interruptAudioPlayer();
    }

    /**
     * Rewinds audiostream to seconds
     * @param seconds
     */
    public void rewindTo(int seconds) {
        threadInterruptionCallback = () -> {
            resetAudioPlayer();
            AudioFormat inputAudioFormat = audioSource.getAudioFormat();
            //cut of frame rate
            long frameRate = (long) inputAudioFormat.getFrameRate();
            int frameSize = inputAudioFormat.getFrameSize();
            long bytesToSkip = (seconds - 1) * (frameSize * frameRate);
            try {
                long skipped = audioSource.getAudioStream().skip(bytesToSkip);
                if(skipped != bytesToSkip) {
                    throw new IOException(String.format("Could not skip audio. Skipped %s of %s", skipped, bytesToSkip));
                }
                totalBytesRead = bytesToSkip;
                //play sound. Do not call start, as we are already in the audioThread.
                run();
            } catch (IOException e) {
                log.warn(String.format("Cannot rewind audio %s", audioSource));
            }
        };
        interruptAudioPlayer();
    }

    @Override
    public void run() {
        AudioProgress currentAudioProgress = getCurrentAudioProgress();
        //if we reached the end of the audio, we start from the beginning
        if(totalBytesRead == currentAudioProgress.getTotalBytes()) {
            resetAudioPlayer();
        } else {
            //get a new audiostream without resetting progress
            audioSource.resetAudioStream();
        }

        AudioInputStream audioInputStream = audioSource.getAudioStream();
        if(totalBytesRead > 0) {
            try {
                audioInputStream.skip(totalBytesRead);
            } catch (IOException e) {
                log.error(String.format("Cannot skip bytes for audio %s. Stopping audio thread %s.", audioSource, audioThread.getName()));
            }
        }
        try {
            sourceDataLine.open(outputAudioFormat);
            sourceDataLine.start();
            interrupted = false;
            while(!interrupted && ((bytesRead = audioInputStream.read(buffer, 0, buffer.length)) != -1))
            {
                // underrun?
                if (sourceDataLine.available() >= sourceDataLine.getBufferSize())
                    log.info("Underrun detected: available=" + sourceDataLine.available() + " , sourcedataline buffer=" + sourceDataLine.getBufferSize());

                // It is possible at this point manipulate the data in buffer[].
                // The write operation blocks while the system plays the sound.
                sourceDataLine.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                notifyProgressListeners();
            }
        } catch (LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            // release resources
            try {
                audioInputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            sourceDataLine.drain();
            sourceDataLine.stop();
            sourceDataLine.close();
        }

        //the thread should execute the interrupt routine by himself to avoid blocking
        // and be sure that the running action was stopped properly.
        if(interrupted && threadInterruptionCallback != null) {
            threadInterruptionCallback.run();
        }
    }

    private void interruptAudioPlayer() {
        interrupted = true;
    }

    private void resetAudioPlayer() {
        bytesRead = 0;
        totalBytesRead = 0;
        buffer = new byte[AUDIO_BUFFER_DEFAULT_SIZE];
        audioSource.resetAudioStream();
        notifyProgressListeners();
    }

    private void notifyProgressListeners() {
        AudioProgress audioProgress = getCurrentAudioProgress();
        audioProgressListeners.forEach(listener -> listener.onAudioProgressChanged(audioProgress));
    }

    public AudioProgress getCurrentAudioProgress() {
        AudioFormat inputAudioFormat = audioSource.getAudioFormat();
        long outputFrameRate = (long) outputAudioFormat.getFrameRate();
        return AudioProgress.builder()
                .playBackRate(playBackRate)
                .frameSize(inputAudioFormat.getFrameSize())
                .frameRate(outputAudioFormat.getFrameRate())
                .framesElapsed(totalBytesRead / (inputAudioFormat.getFrameSize()))
                .secondsElapsed(totalBytesRead / (outputFrameRate * inputAudioFormat.getFrameSize()))
                .bytesElapsed(totalBytesRead)
                .totalSeconds(audioSource.getAudioStream().getFrameLength() / outputFrameRate)
                .totalFrames(audioSource.getAudioStream().getFrameLength())
                .totalBytes(audioSource.getAudioStream().getFrameLength() * inputAudioFormat.getFrameSize())
                .latestByteBuffer(buffer)
                .build();
    }

    //TODO: do not share Audiosource directly -> modify controllers and make private
    public AudioSource getAudioSource() {
        return audioSource;
    }
}
