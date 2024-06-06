package com.example.aircraftwar2024.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aircarftwar2024.R;
import com.example.aircraftwar2024.game.BaseGame;

public class OfflineActivity extends AppCompatActivity {
    public static int gameType=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        Button easyModeButton = findViewById(R.id.easyModeButton);
        Button normalModeButton = findViewById(R.id.normalModeButton);
        Button hardModeButton = findViewById(R.id.hardModeButton);

        // 接收 Intent 传递的参数
        boolean isMusicOn = getIntent().getBooleanExtra("isMusicOn", false);

        // 根据音乐开关状态执行相应的操作，例如播放或停止音乐
        if (isMusicOn) {
            // 启动音乐
            Toast.makeText(this, "音乐已开启", Toast.LENGTH_SHORT).show();
            BaseGame.isSoundOn = true;

        } else {
            // 停止音乐
            Toast.makeText(this, "音乐已关闭", Toast.LENGTH_SHORT).show();
            BaseGame.isSoundOn = false;
        }

        easyModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameType=1;
                startGameActivity(1);  // 简单模式
            }
        });

        normalModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameType=2;
                startGameActivity(2);  // 普通模式
            }
        });

        hardModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameType=3;
                startGameActivity(3);  // 困难模式
            }
        });
    }
    private void startGameActivity(int gameType) {
        Intent intent = new Intent(OfflineActivity.this, GameActivity.class);
        intent.putExtra("gameType", gameType);
        startActivity(intent);
    }
}


