package at.jku.audioeditor.model;

import javax.sound.sampled.AudioFormat;

/**
 * Created by IntelliJ IDEA.
 * User    : sfeiler
 * Date    : 12/05/2025
 * Project : Audio_Editor
 */
public interface AudioFilter {
    boolean isActive();
    void setActive(boolean active);
    void apply(byte[] data, AudioFormat audioFormat);
}
