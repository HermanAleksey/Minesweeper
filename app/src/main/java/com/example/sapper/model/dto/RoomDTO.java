package com.example.sapper.model.dto;

import java.io.Serializable;

public class RoomDTO implements Serializable {
    int id;
    PlayerDTO player_1;
    PlayerDTO player_2;

    public RoomDTO() {
    }

    public RoomDTO(int id, PlayerDTO player_1, PlayerDTO player_2) {
        this.id = id;
        this.player_1 = player_1;
        this.player_2 = player_2;
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

    @Override
    public String toString() {
        return "RoomDTO{" +
                "id=" + id +
                ", player_1=" + player_1 +
                ", player_2=" + player_2 +
                '}';
    }
}
