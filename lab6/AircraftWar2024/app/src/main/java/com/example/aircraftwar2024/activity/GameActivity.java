package com.example.aircraftwar2024.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aircraftwar2024.DAO.User;
import com.example.aircraftwar2024.game.BaseGame;
import com.example.aircraftwar2024.game.EasyGame;
import com.example.aircraftwar2024.game.HardGame;
import com.example.aircraftwar2024.game.MediumGame;

import java.time.LocalDate;


public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";

    private int gameType=0;
    public static int screenWidth,screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getScreenHW();

        Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                ActivityManager.getActivityManager().addActivity(GameActivity.this);
                Log.d(TAG, "handleMessage");
                if(msg.what == 1){
                    Intent intent = new Intent(GameActivity.this, RecordActivity.class);

                    User user = (User)msg.obj;
                    intent.putExtra("user_score", user.getScore());
                    intent.putExtra("user_time", user.getWriteTime());

                    Toast.makeText(GameActivity.this, "GameOver", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            }
        };

        if(getIntent() != null){
            gameType = getIntent().getIntExtra("gameType",1);
        }

        /*TODO:根据用户选择的难度加载相应的游戏界面*/
        if(getIntent() != null){
            gameType = getIntent().getIntExtra("gameType", 1);
        }
        BaseGame baseGameView = null;
        if(gameType == 1){
            baseGameView = new EasyGame(this, handler);
        } else if (gameType == 2) {
            baseGameView = new MediumGame(this, handler);
        }else{
            baseGameView = new HardGame(this, handler);
        }
        setContentView(baseGameView);
    }

    public void getScreenHW(){
        //定义DisplayMetrics 对象
        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        getDisplay().getRealMetrics(dm);

        //窗口的宽度
        screenWidth= dm.widthPixels;
        //窗口高度
        screenHeight = dm.heightPixels;

        Log.i(TAG, "screenWidth : " + screenWidth + " screenHeight : " + screenHeight);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}