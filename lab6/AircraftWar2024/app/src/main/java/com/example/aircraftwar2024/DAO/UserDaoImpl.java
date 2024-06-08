package com.example.aircraftwar2024.DAO;

import android.content.Context;

import java.io.*;
import java.util.*;

public class UserDaoImpl implements UserDao {
    // 模拟数据库数据
    private List<User> userList = new ArrayList<>();
    private Context context;
    private String filename;

    public UserDaoImpl(Context context, String filename) throws IOException {
        this.context = context;
        this.filename = filename;

        // 确保文件存在
        ensureFileExists();

        try (ObjectInputStream ois = new ObjectInputStream(context.openFileInput(this.filename))){
            userList = (List<User>) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // 确保文件存在的方法
    private void ensureFileExists() throws IOException {
        File file = new File(context.getFilesDir(), filename);
        if (!file.exists()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput(filename, Context.MODE_PRIVATE))) {
                // 在这里初始化文件内容，例如，写入一个空列表
                List<User> emptyList = new ArrayList<>();
                oos.writeObject(emptyList);
            }
        }
    }

    @Override
    public void addData(User user) throws IOException, ClassNotFoundException {
        // 添加当前数据
        userList.add(user);
        storageData(userList);
    }

    @Override
    public void deleteData(int num){
        userList.remove(userList.get(num));

        Collections.sort(userList, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o2.getScore() - o1.getScore();
            }
        });
    }

    @Override
    public void storageData(List<User> userList) throws IOException {
        // 根据分数排序
        Collections.sort(userList, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o2.getScore() - o1.getScore();
            }
        });
        // 将榜单写回文件
        ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput(this.filename, Context.MODE_PRIVATE));
        oos.writeObject(userList);
        oos.close();
    }

    @Override
    public List<User> getAllData(){
        return userList;
    }
}
