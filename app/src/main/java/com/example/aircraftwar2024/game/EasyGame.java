package com.example.aircraftwar2024.game;

import android.content.Context;
import android.os.Handler;

import com.example.aircraftwar2024.ImageManager;
import com.example.aircraftwar2024.aircraft.AbstractEnemyAircraft;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


public class EasyGame extends BaseGame{

    public EasyGame(Context context, Handler handler)  throws IOException, ClassNotFoundException {
        super(context,handler);
        this.backGround = ImageManager.BACKGROUND1_IMAGE;
        this.enemyMaxNumber = 2;
    }

    @Override
    protected void tick() {
    }

    /**
     * 简单模式没有 boss
     * @return
     */
    @Override
    protected List<AbstractEnemyAircraft> produceBoss() {
        return new LinkedList<>();
    }


}
