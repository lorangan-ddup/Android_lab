package com.example.aircraftwar2024.activity;

import android.app.LauncherActivity;
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

import java.io.IOException;


public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";

    private int gameType=0;
    public static int screenWidth,screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getScreenHW();
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(TAG,"handleMessage");
                if (msg.what == 1) {
                    if(!MainActivity.isOnline){
                        Intent intent = new Intent(GameActivity.this,RecordActivity.class);

                        User user = (User)msg.obj;
                        if (user != null) {
                            // 检查user对象是否为null
                            intent.putExtra("user_score", user.getScore());
                            intent.putExtra("user_time", user.getTime());
                        } else {
                            // 如果user对象为null，记录日志或处理这种情况
                            Log.e(TAG, "User object is null");
                            // 可以在这里处理user为null的情况，例如：
                            intent.putExtra("user_score", 0);
                            intent.putExtra("user_time", "00:00:00");
                        }

                        Toast.makeText(GameActivity.this,"GameOver",Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                }
            }
        };
        if(getIntent() != null){
            gameType = getIntent().getIntExtra("gameType",1);
        }

        /*TODO:根据用户选择的难度加载相应的游戏界面*/

        BaseGame baseGameView = null;
        switch (gameType) {
            case 1:
                try{
                    baseGameView = new EasyGame(this,handler);
                }catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try{
                    baseGameView = new MediumGame(this,handler);
                }catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                try{
                    baseGameView = new HardGame(this,handler);
                }catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                try{
                    baseGameView = new EasyGame(this,handler);
                }catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
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