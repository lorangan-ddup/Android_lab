package com.example.aircraftwar2024.music;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.example.aircraftwar2024.R;

import java.util.HashMap;

public class MySoundPool {
    private SoundPool soundPool;
    private AudioAttributes audioAttributes = null;
    private HashMap<Integer,Integer> soundPoolMap;
    public MySoundPool(Context context){
        audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build();

        soundPoolMap = new HashMap<>();
        soundPoolMap.put(1, soundPool.load(context, R.raw.bomb_explosion, 1));
        soundPoolMap.put(2, soundPool.load(context, R.raw.bullet_hit, 1));
        soundPoolMap.put(3, soundPool.load(context, R.raw.get_supply, 1));
        soundPoolMap.put(4, soundPool.load(context, R.raw.game_over, 1));
    }
    public void explosionSP(){
        soundPool.play(soundPoolMap.get(1), 1, 1, 1, 0, 1.2f);
    }
    public void hitSP(){
        soundPool.play(soundPoolMap.get(2), 1, 1, 1, 0, 1.2f);
    }
    public void supplySP(){
        soundPool.play(soundPoolMap.get(3), 1, 1, 1, 0, 1.2f);
    }
    public void gameOverBgm(){
        soundPool.play(soundPoolMap.get(4), 1, 1, 1, 0, 1.2f);
    }
}
