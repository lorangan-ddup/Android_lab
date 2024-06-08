package com.example.aircraftwar2024.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.aircraftwar2024.R;
import com.example.aircraftwar2024.game.BaseGame;
import com.example.aircraftwar2024.game.MediumGame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Button startButton;
    Button onlineGame;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Handler handler;

    private BaseGame game;
    private static int opponentScore = 0;
    private static boolean gameOverFlag = false;
    public static boolean isOnline = false;
    public static boolean isMusicNeeded = false;
    public static final String IP = "10.0.2.2";
    private AlertDialog alertDialog;

    private TextView opponentScoreTextView;
    private TextView myScoreTextView;
    private Handler scoreUpdateHandler;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getActivityManager().addActivity(MainActivity.this);
        setContentView(R.layout.activity_main);

        // 设置默认选中的 RadioButton
        RadioButton closeMusicRadioButton = findViewById(R.id.close_music);
        closeMusicRadioButton.setChecked(true);

        startButton = findViewById(R.id.start_btn);
        onlineGame = findViewById(R.id.online_btn);

        // 选择单机模式
        startButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, OfflineActivity.class);
            // 获取音乐控制 RadioGroup 对象
            RadioGroup musicControlGroup = findViewById(R.id.music_control_group);
            // 获取选中的 RadioButton 的 ID
            int checkedRadioButtonId = musicControlGroup.getCheckedRadioButtonId();
            boolean isMusicOn = checkedRadioButtonId == R.id.start_music;
            intent.putExtra("is_music_on", isMusicOn);
            startActivity(intent);
        });

        // 用于发送接收到的服务器端的消息，显示在界面上
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // 启动游戏
                if (msg.what == 0x123 && msg.obj.equals("start")) {
                    // 连接成功，关闭匹配中对话框
                    dismissMatchingDialog();
                    game = new MediumGame(MainActivity.this, handler);
                    setContentView(game);

                    // 如果开启游戏，那么就新开一个线程给服务端发送当前分数
                    // 如果当前玩家已经死亡，那么就给服务器传"end"信息
                    new Thread(() -> {
                        // 发送当前分数
                        while (!game.isGameOverFlag()) {
                            writer.println(BaseGame.score);
                            Log.i(TAG, "send to server: score " + BaseGame.score);
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        // 死亡后发送结束标志
                        writer.println("end");
                        Log.i(TAG, "send to server: end");

                        runOnUiThread(() -> {
                            setContentView(R.layout.activity_end);

                            // 获取 TextView
                            opponentScoreTextView = findViewById(R.id.opponent_score_textview);
                            myScoreTextView = findViewById(R.id.eternity_score_textview);

                            // 显示自己的分数
                            myScoreTextView.setText("My Score: " + BaseGame.score);

                            // 创建一个 Handler 和 Runnable 来更新对方分数
                            scoreUpdateHandler = new Handler(Looper.getMainLooper());
                            Runnable scoreUpdater = new Runnable() {
                                @Override
                                public void run() {
                                    if (opponentScoreTextView != null) {
                                        opponentScoreTextView.setText("Opponent Score: " + opponentScore);
                                    }
                                    // 定期刷新，每隔0.1秒更新一次
                                    scoreUpdateHandler.postDelayed(this, 100);
                                }
                            };
                            scoreUpdateHandler.post(scoreUpdater);
                        });

                    }).start();

                } else if (msg.what == 0x123 && msg.obj.equals("gameover")) {
                    // 设置标志：双方游戏全部结束
                    setGameOverFlag(true);
                    Log.i(TAG, "游戏结束");

                    Intent intent = new Intent(MainActivity.this, OverActivity.class);
                    intent.putExtra("myScore", BaseGame.score);
                    intent.putExtra("otherScore", opponentScore);
                    startActivity(intent);
                    Log.i(TAG, "跳转");

                }else {
                    try {
                        if ((String) msg.obj != null) {
                            opponentScore = Integer.parseInt((String) msg.obj);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        // 选择联机模式
        onlineGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取音乐控制 RadioGroup 对象
                RadioGroup musicControlGroup = findViewById(R.id.music_control_group);
                // 获取选中的 RadioButton 的 ID
                int checkedRadioButtonId = musicControlGroup.getCheckedRadioButtonId();
                isMusicNeeded = checkedRadioButtonId == R.id.start_music;
                isOnline = true;
                showMatchingDialog();
                new Thread(new ClientThread(handler)).start();
            }
        });
    }

    private void showMatchingDialog() {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("匹配中，请等待……");
            builder.setCancelable(false); // 设置为不可取消
            alertDialog = builder.create();
            alertDialog.show();
        });
    }

    private void dismissMatchingDialog() {
        runOnUiThread(() -> {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        });
    }

    class ClientThread implements Runnable {
        private Handler handler;    // 向客户端的UI发送消息

        public ClientThread(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            try {
                // 连接到服务器
                socket = new Socket();
                socket.connect(new InetSocketAddress(IP, 9999), 5000);
                writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                        socket.getOutputStream(), StandardCharsets.UTF_8)), true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

                // 创建子线程接收服务端信息
                // 服务器端可能回复的消息："start"/"end"/不发送，此时显示对手名称及分数
                new Thread(() -> {
                    String msg;
                    try {
                        while ((msg = reader.readLine()) != null) {
                            Log.e(TAG, "get from server: " + msg);

                            Message msgFromServer = new Message();
                            msgFromServer.what = 0x123;
                            msgFromServer.obj = msg;
                            handler.sendMessage(msgFromServer);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

            } catch (IOException e) {
                e.printStackTrace();
                dismissMatchingDialog();
                // 提示连接失败
                runOnUiThread(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("连接失败");
                    builder.setMessage("无法连接到服务器，请稍后再试。");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                });
            }
        }
    }

    public static int getOpponentScore() {
        return opponentScore;
    }

    private void setGameOverFlag(boolean gameOverFlag) {
        MainActivity.gameOverFlag = gameOverFlag;
    }

    public static boolean isGameOverFlag() {
        return gameOverFlag;
    }
}

