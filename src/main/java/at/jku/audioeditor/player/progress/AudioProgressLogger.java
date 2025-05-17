package at.jku.audioeditor.player.progress;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by IntelliJ IDEA.
 * User    : sfeiler
 * Date    : 17/05/2025
 * Project : Audio_Editor
 */

@Slf4j
public class AudioProgressLogger implements AudioProgressListener {
    public AudioProgressLogger() {
    }

    @Override
    public void onAudioProgressChanged(AudioProgress audioProgress) {
        log.info("onAudioProgressChanged: \n" + audioProgress);
    }
}
