package com.example.sapper.model.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RoomDTO implements Serializable {
    int id;
    PlayerDTO player_1;
    PlayerDTO player_2;
    @SerializedName("width")
    @Expose
    private int width;
    @SerializedName("height")
    @Expose
    private int height;
    @SerializedName("minesCount")
    @Expose
    private int minesCount;
    @SerializedName("timeMin")
    @Expose
    private int timeMin;
    @SerializedName("timeSec")
    @Expose
    private int timeSec;
    @SerializedName("sameField")
    @Expose
    private boolean sameField;

    public RoomDTO() {
    }

//    public RoomDTO(BluetoothGame game) {
//        this.player_1 = player_1;
//        this.player_2 = player_2;
//        this.width = width;
//        this.height = height;
//        this.minesCount = minesCount;
//        this.timeMin = timeMin;
//        this.timeSec = timeSec;
//        this.sameField = sameField;
//    }

    public RoomDTO(int id, PlayerDTO player_1, PlayerDTO player_2, int width, int height, int minesCount, int timeMin, int timeSec, boolean sameField) {
        this.id = id;
        this.player_1 = player_1;
        this.player_2 = player_2;
        this.width = width;
        this.height = height;
        this.minesCount = minesCount;
        this.timeMin = timeMin;
        this.timeSec = timeSec;
        this.sameField = sameField;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PlayerDTO getPlayer_1() {
        return player_1;
    }

    public void setPlayer_1(PlayerDTO player_1) {
        this.player_1 = player_1;
    }

    public PlayerDTO getPlayer_2() {
        return player_2;
    }

    public void setPlayer_2(PlayerDTO player_2) {
        this.player_2 = player_2;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMinesCount() {
        return minesCount;
    }

    public void setMinesCount(int minesCount) {
        this.minesCount = minesCount;
    }

    public int getTimeMin() {
        return timeMin;
    }

    public void setTimeMin(int timeMin) {
        this.timeMin = timeMin;
    }

    public int getTimeSec() {
        return timeSec;
    }

    public void setTimeSec(int timeSec) {
        this.timeSec = timeSec;
    }

    public boolean isSameField() {
        return sameField;
    }

    public void setSameField(boolean sameField) {
        this.sameField = sameField;
    }

    @Override
    public String toString() {
        return "RoomDTO{" +
                "id=" + id +
                ", player_1=" + player_1 +
                ", player_2=" + player_2 +
                ", width=" + width +
                ", height=" + height +
                ", minesCount=" + minesCount +
                ", timeMin=" + timeMin +
                ", timeSec=" + timeSec +
                ", sameField=" + sameField +
                '}';
    }
}
