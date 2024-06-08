package com.example.aircraftwar2024.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aircraftwar2024.DAO.User;
import com.example.aircraftwar2024.DAO.UserDao;
import com.example.aircraftwar2024.DAO.UserDaoImpl;
import com.example.aircraftwar2024.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RecordActivity extends AppCompatActivity {
    UserDao userDao;
    User user;
    List<User> playerList = null;
    String name;
    int score;
    String time;
    Button returnButton;

    TextView titleTextView; // 定义 TextView
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getActivityManager().addActivity(RecordActivity.this);
        setContentView(R.layout.activity_record);

        titleTextView = findViewById(R.id.textView2); // 获取 TextView
        returnButton = findViewById(R.id.return_btn);

        switch (OfflineActivity.gameType){
            case 1:
                titleTextView.setText("简单模式");
                try {
                    userDao = new UserDaoImpl(this, "simpledata.dat");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 2:
                titleTextView.setText("普通模式");
                try {
                    userDao = new UserDaoImpl(this, "mediumdata.dat");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 3:
                titleTextView.setText("困难模式");
                try {
                    userDao = new UserDaoImpl(this, "harddata.dat");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                System.out.println("ERROR");
        }

        user = new User();
        score = getIntent().getIntExtra("user_score", 0);
        time = getIntent().getStringExtra("user_time");

        user.setName("test");
        user.setScore(score);
        user.setWriteTime(time);

        try {
            userDao.addData(user);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        showList();

        returnButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(RecordActivity.this, MainActivity.class);
                // startActivity(intent);
                ActivityManager.getActivityManager().finishActivity(RecordActivity.this);
                ActivityManager.getActivityManager().finishActivity(GameActivity.class);
                ActivityManager.getActivityManager().finishActivity(OfflineActivity.class);
            }
        });
    }

    public void showList(){

        String[] simpleCursor = new String[] {"rank", "name", "score", "time"};

        playerList = userDao.getAllData();

        ListView listView = findViewById(R.id.list);
        List<Map<String, String>> maps = new LinkedList<>();
        int rank = 1;
        for (User curPlayer : playerList) {
            Map<String, String> curMap = new HashMap<>();
            curMap.put("rank", String.valueOf(rank));
            curMap.put("name", curPlayer.getName());
            curMap.put("score", String.valueOf(curPlayer.getScore()));
            curMap.put("time", curPlayer.getWriteTime());
            maps.add(curMap);
            rank++;
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this, maps, R.layout.activity_item,
                simpleCursor, new int[] {R.id.rank, R.id.name, R.id.score, R.id.time}
        );


        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {

                AlertDialog alertDialog = new AlertDialog.Builder(RecordActivity.this)
                        .setTitle("提示")
                        .setMessage("确认删除该条记录吗")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j) {
                                maps.remove(position);

                                //更新排行榜文件
                                try {
                                    playerList.remove(position);
                                    userDao.storageData(playerList);
                                    showList();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                adapter.notifyDataSetChanged();
                            }
                        })

                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });
    }

}