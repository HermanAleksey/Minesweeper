package com.example.sapper.network.api;

import com.example.sapper.model.dto.RoomDTO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface SaperApi {


    @GET("/api/v1/saper/test")
    Call<String> test();

    @GET("/api/v1/saper/room/create")
    Call<RoomDTO> createRoom();

    @GET("/api/v1/saper/rooms")
    Call<ArrayList<RoomDTO>> selectAllRooms();

}
