package com.example.aircraftwar2024.DAO;

import java.io.Serializable;

public class User implements Serializable {
    public String name;
    public int score;
    public int rank;
    public String overTime;

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore(){
        return score;
    }

    public void setRank(int rank){
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public void setOverTime(String overTime) {
        this.overTime = overTime;
    }

    public String getTime(){
        return overTime;
    }

}
