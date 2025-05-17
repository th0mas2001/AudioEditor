package at.jku.audioeditor.player.progress;

import lombok.Builder;

/**
 * Created by IntelliJ IDEA.
 * User    : sfeiler
 * Date    : 17/05/2025
 * Project : Audio_Editor
 */

@Builder
public class AudioProgress {
    final double playBackRate;
    final int frameSize;
    final float frameRate;
    final long framesElapsed;
    final long secondsElapsed;
    final long bytesElapsed;
    final long totalSeconds;
    final long totalFrames;
    final long totalBytes;
    final byte[] latestByteBuffer;

    public double getPlayBackRate() {
        return playBackRate;
    }

    public int getFrameSize() {
        return frameSize;
    }

    public float getFrameRate() {
        return frameRate;
    }

    public long getFramesElapsed() {
        return framesElapsed;
    }

    public long getSecondsElapsed() {
        return secondsElapsed;
    }

    public long getBytesElapsed() {
        return bytesElapsed;
    }

    public long getTotalSeconds() {
        return totalSeconds;
    }

    public long getTotalFrames() {
        return totalFrames;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public byte[] getLatestByteBuffer() {
        return latestByteBuffer;
    }

    public boolean audioFinished() {
        return bytesElapsed == totalBytes;
    }

    @Override
    public String toString() {
        return "AudioProgressChangedEvent{" +
                "secondsElapsed=" + secondsElapsed +
                ", totalSeconds=" + totalSeconds +
                ", playBackRate=" + playBackRate +
                ", frameSize=" + frameSize +
                ", frameRate=" + frameRate +
                ", framesElapsed=" + framesElapsed +
                ", totalFrames=" + totalFrames +
                ", totalBytes=" + totalBytes +
                ", latestByteBufferLength=" + latestByteBuffer.length +
                '}';
    }
}
