package com.example.aircraftwar2024.activity;

import android.app.AlertDialog;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aircarftwar2024.R;
import com.example.aircraftwar2024.DAO.User;
import com.example.aircraftwar2024.DAO.UserDao;
import com.example.aircraftwar2024.DAO.UserDaoImpl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

    private void updateRankings() {
        // 按照分数降序排列玩家列表
        Collections.sort(playerList, new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                // 降序排序分数
                return Integer.compare(user2.getScore(), user1.getScore());
            }
        });

        // 更新排名
        for (int i = 0; i < playerList.size(); i++) {
            playerList.get(i).setRank(i + 1);
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        // 添加当前Activity到ActivityManager
        ActivityManager.getActivityManager().addActivity(this);

        returnButton = findViewById(R.id.return_btn);

        switch (OfflineActivity.gameType) {
            case 1:
                userDao = new UserDaoImpl(this, "simpledata.dat");
                break;
            case 2:
                userDao = new UserDaoImpl(this, "mediumdata.dat");
                break;
            case 3:
                userDao = new UserDaoImpl(this, "harddata.dat");
                break;
            default:
                System.out.println("ERROR");
        }

        user = new User();
        score = getIntent().getIntExtra("user_score", 0);
        time = getIntent().getStringExtra("user_time");
        inputName();

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 使用ActivityManager返回到MainActivity
                ActivityManager.getActivityManager().finishActivity(RecordActivity.this);
                Intent intent = new Intent(RecordActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showList() {
        String[] simpleCursor = new String[]{"rank","name", "score", "time"};

        try {
            playerList = userDao.getAllData();
            updateRankings();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ListView listView = findViewById(R.id.list);
        List<Map<String, String>> maps = new LinkedList<>();
        for (User curPlayer : playerList) {
            Map<String, String> curMap = new HashMap<>();
            curMap.put("rank", String.valueOf(curPlayer.getRank()));
            curMap.put("name", curPlayer.getName());
            curMap.put("score", String.valueOf(curPlayer.getScore()));
            curMap.put("time", curPlayer.getTime());
            maps.add(curMap);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this, maps, R.layout.record_item,
                simpleCursor, new int[]{R.id.rank,R.id.name, R.id.score, R.id.time}
        );

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                AlertDialog alertDialog = new AlertDialog.Builder(RecordActivity.this)
                        .setTitle("提示")
                        .setMessage("确认删除该条记录吗")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j) {
                                //maps.remove(position);
                                // 更新排行榜文件
                                try {
                                    playerList.remove(position);
                                    updateRankings();
                                    userDao.storage(playerList);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                showList();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // 取消操作
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });
    }

    private void inputName() {
        EditText input = new EditText(this);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("请输入名字以记录得分")
                .setView(input);

        builder.setPositiveButton("确定", (dialogInterface, i) -> {
            name = input.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(this, "输入为空", Toast.LENGTH_SHORT).show();
            } else {
                user.setName(name);
                user.setScore(score);
                user.setOverTime(time);
                try {
                    userDao.addData(user);
                    Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                showList();
            }
        });

        builder.setNegativeButton("取消", (dialogInterface, i) -> {
            Toast.makeText(this, "您还未输入姓名", Toast.LENGTH_SHORT).show();
            showList();
        });

        builder.show();
    }
}
