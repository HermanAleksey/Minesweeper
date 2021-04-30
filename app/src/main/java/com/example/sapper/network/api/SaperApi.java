package com.example.sapper.network.api;

import com.example.sapper.model.dto.RoomDTO;
import com.example.sapper.model.dto.WebGameDto;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface SaperApi {


    @GET("/api/v1/saper/test")
    Call<String> test();

    @POST("/api/v1/saper/room/create")
    Call<RoomDTO> createRoom(@Body WebGameDto webGameDto);

    @GET("/api/v1/saper/rooms")
    Call<ArrayList<RoomDTO>> selectAllRooms();

}
