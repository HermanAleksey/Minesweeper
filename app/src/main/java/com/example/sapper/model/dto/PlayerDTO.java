package com.example.sapper.model.dto;

import java.io.Serializable;

public class PlayerDTO implements Serializable {
    private Long id;
    private String username;
    private int winsScore;
    private int losesScore;

    public PlayerDTO() {
    }

    public PlayerDTO(Long id, String username, int winsScore, int losesScore) {
        this.id = id;
        this.username = username;
        this.winsScore = winsScore;
        this.losesScore = losesScore;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getWinsScore() {
        return winsScore;
    }

    public void setWinsScore(int winsScore) {
        this.winsScore = winsScore;
    }

    public int getLosesScore() {
        return losesScore;
    }

    public void setLosesScore(int losesScore) {
        this.losesScore = losesScore;
    }

    @Override
    public String toString() {
        return "PlayerDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", winsScore=" + winsScore +
                ", losesScore=" + losesScore +
                '}';
    }
}
