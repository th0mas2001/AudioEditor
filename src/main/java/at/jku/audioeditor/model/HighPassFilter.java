package at.jku.audioeditor.model;

import javax.sound.sampled.AudioFormat;

/**
 * Created by IntelliJ IDEA.
 * User    : sfeiler
 * Date    : 12/05/2025
 * Project : Audio_Editor
 */
public class HighPassFilter implements AudioFilter {
    boolean active = false;

    public HighPassFilter(boolean active) {
        this.active = active;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void apply(byte[] data, AudioFormat audioFormat) {

    }
}
