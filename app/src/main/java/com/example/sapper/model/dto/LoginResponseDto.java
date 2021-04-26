package com.example.sapper.model.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponseDto {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("userId")
    @Expose
    private Long userId;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("description")
    @Expose
    private String description;


    public LoginResponseDto() {
    }

    public LoginResponseDto(String status, Long userId, String username, String token, String description) {
        this.status = status;
        this.userId = userId;
        this.username = username;
        this.token = token;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "LoginResponseDto{" +
                "status='" + status + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", token='" + token + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
