package com.example.loongfly.mybs.entity;

import java.util.Date;

/**
 * Created by Loong Fly on 2018/2/20.
 */

public class temp_data {
    public int id;
    public float tempNum;
    public Date time;

    public temp_data(int id, float tempNum, Date time) {
        super();
        this.id = id;
        this.tempNum = tempNum;
        this.time = time;
    }

    public temp_data() {
        super();
        // TODO Auto-generated constructor stub
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Date getTime() {
        return time;
    }
    public void setTime(Date time) {
        this.time = time;
    }
    public float getTempNum() {
        return tempNum;
    }
    public void setTempNum(float tempNum) {
        this.tempNum = tempNum;
    }

}
