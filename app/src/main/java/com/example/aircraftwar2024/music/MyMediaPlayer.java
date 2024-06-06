package com.example.aircraftwar2024.music;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.aircarftwar2024.R;

public class MyMediaPlayer {
    private MediaPlayer bgmPlayer;
    private MediaPlayer bossBgmPlayer;
    private Context context;
    private int bgmPosition = 0;
    private int bossBgmPosition = 0;

    public MyMediaPlayer(Context context) {
        this.context = context;
        this.bgmPlayer = MediaPlayer.create(context, R.raw.bgm);
        this.bossBgmPlayer = MediaPlayer.create(context, R.raw.bgm_boss);
    }

    public void playBackgroundMusic() {
        if (!bgmPlayer.isPlaying()) {
            bgmPlayer.seekTo(bgmPosition);
            bgmPlayer.start();
            bgmPlayer.setLooping(true);
        }
    }

    public void pauseBackgroundMusic() {
        if (bgmPlayer.isPlaying()) {
            bgmPosition = bgmPlayer.getCurrentPosition();
            bgmPlayer.pause();
        }
    }

    public void stopBackgroundMusic() {
        if (bgmPlayer.isPlaying()) {
            bgmPlayer.stop();
            bgmPlayer.release();
            bgmPlayer = MediaPlayer.create(context, R.raw.bgm);
            bgmPosition = 0;
        }
    }

    public void playBossMusic() {
        if (bgmPlayer.isPlaying()) {
            pauseBackgroundMusic();
        }
        if (!bossBgmPlayer.isPlaying()) {
            bossBgmPlayer.seekTo(bossBgmPosition);
            bossBgmPlayer.start();
            bossBgmPlayer.setLooping(true);
        }
    }

    public void pauseBossMusic() {
        if (bossBgmPlayer.isPlaying()) {
            bossBgmPosition = bossBgmPlayer.getCurrentPosition();
            bossBgmPlayer.pause();
        }
    }

    public void stopBossMusic() {
        if (bossBgmPlayer.isPlaying()) {
            bossBgmPlayer.stop();
            bossBgmPlayer.release();
            bossBgmPlayer = MediaPlayer.create(context, R.raw.bgm_boss);
            bossBgmPosition = 0;
        }
    }

    public void release() {
        if (bgmPlayer != null) {
            bgmPlayer.release();
            bgmPlayer = null;
        }
        if (bossBgmPlayer != null) {
            bossBgmPlayer.release();
            bossBgmPlayer = null;
        }
    }
}
