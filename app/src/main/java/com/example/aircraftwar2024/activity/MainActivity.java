package com.example.aircraftwar2024.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aircarftwar2024.R;

public class MainActivity extends AppCompatActivity {

    private RadioGroup musicRadioGroup;
    public static boolean isOnline =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicRadioGroup = findViewById(R.id.musicRadioGroup);

        Button offlineGameButton = findViewById(R.id.singlePlayerButton);
        Button onlineGameButton = findViewById(R.id.multiPlayerButton);
        offlineGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取音乐开关状态
                int selectedMusicOptionId = musicRadioGroup.getCheckedRadioButtonId();
                boolean isMusicOn = selectedMusicOptionId == R.id.musicOn;

                // 创建 Intent 以启动 OfflineActivity
                Intent intent = new Intent(MainActivity.this, OfflineActivity.class);
                // 传递音乐开关状态
                intent.putExtra("isMusicOn", isMusicOn);
                startActivity(intent);
            }
        });
        onlineGameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isOnline = true;
                // 获取音乐开关状态
                int selectedMusicOptionId = musicRadioGroup.getCheckedRadioButtonId();
                boolean isMusicOn = selectedMusicOptionId == R.id.musicOn;

                // 创建 Intent 以启动 OfflineActivity
                Intent intent = new Intent(MainActivity.this, OfflineActivity.class);
                // 传递音乐开关状态
                intent.putExtra("isMusicOn", isMusicOn);
                startActivity(intent);
            }
        });
    }
}
