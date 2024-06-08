package com.example.aircraftwar2024.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.aircraftwar2024.R;

public class OfflineActivity extends AppCompatActivity {
    public static int gameType=0;
    public static boolean isMusicOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getActivityManager().addActivity(OfflineActivity.this);
        setContentView(R.layout.activity_offline);

        Button medium_btn = findViewById(R.id.medium_btn);
        Button easy_btn = findViewById(R.id.easy_btn);
        Button hard_btn = findViewById(R.id.hard_btn);

        Intent intent = new Intent(OfflineActivity.this, GameActivity.class);
        easy_btn.setOnClickListener(view -> {
            gameType = 1;
            intent.putExtra("gameType", gameType);
            startActivity(intent);
        });
        medium_btn.setOnClickListener(view -> {
            gameType=2;
            intent.putExtra("gameType",gameType);
            startActivity(intent);
        });
        hard_btn.setOnClickListener(view -> {
            gameType =3;
            intent.putExtra("gameType",gameType);
            startActivity(intent);
        });

        // 获取从上一个活动传递过来的参数
        isMusicOn = getIntent().getBooleanExtra("is_music_on", false);

        // 现在你可以使用 isMusicOn 来执行你的逻辑了
        if (isMusicOn) {
            // 如果音乐是开启的，执行相关操作
            Log.d("OfflineActivity", "音乐是开启的");
        } else {
            // 如果音乐是关闭的，执行相关操作
            Log.d("OfflineActivity", "音乐是关闭的");
        }
    }
}