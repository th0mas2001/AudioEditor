package at.jku.audioeditor.source;

import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Created by IntelliJ IDEA.
 * User    : sfeiler
 * Date    : 12/05/2025
 * Project : Audio_Editor
 */

@Slf4j
public class FileAudioSource implements AudioSource {
    final String audioId;
    final File file;
    AudioInputStream audioInputStream;
    AudioFormat audioFormat;

    public FileAudioSource(String absoluteFilePath) {
        this(new File(absoluteFilePath));
    }
    
    public FileAudioSource(File file) {
        this.audioId = file.getName();
        this.file = file;
        loadAudioInputStream();
    }

    @Override
    public void resetAudioStream() {
        loadAudioInputStream();
    }

    private void loadAudioInputStream() {
        try {
            if(!file.exists()) {
                throw new IOException(String.format("File %s does not exist", file.getAbsolutePath()));
            }
            audioInputStream = AudioSystem.getAudioInputStream(file);
            audioFormat = audioInputStream.getFormat();
        } catch (UnsupportedAudioFileException e) {
            log.debug(String.format("The provided audio file %s is unsupported.", file.getAbsolutePath()));
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.debug(String.format("Cannot load audio file %s.", file.getAbsolutePath()));
            throw new RuntimeException(e);
        }
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }
    
    @Override
    public AudioInputStream getAudioStream() {
        return audioInputStream;
    }
    
    @Override
    public String audioId() {
        return audioId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(audioId);
    }

    @Override
    public String toString() {
        return "FileAudioSource{" +
                "audioId='" + audioId + '\'' +
                "filePath='" + file.getAbsolutePath() + '\'' +
                '}';
    }
}
