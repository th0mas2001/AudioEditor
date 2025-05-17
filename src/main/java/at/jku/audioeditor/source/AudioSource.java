package at.jku.audioeditor.source;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * Created by IntelliJ IDEA.
 * User    : sfeiler
 * Date    : 13/05/2025
 * Project : Audio_Editor
 */
public interface AudioSource {
    String audioId();
    void resetAudioStream();
    AudioInputStream getAudioStream();
    AudioFormat getAudioFormat();
}
