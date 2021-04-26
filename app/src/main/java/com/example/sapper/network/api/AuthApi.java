package com.example.sapper.network.api;

import com.example.sapper.model.dto.AuthenticationRequestDto;
import com.example.sapper.model.dto.LoginResponseDto;
import com.example.sapper.model.dto.RegistrationRequestDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("/api/v1/auth/login")
    Call<LoginResponseDto> login(@Body AuthenticationRequestDto body);

    @POST("/api/v1/auth/registration")
    Call<LoginResponseDto> registration(@Body RegistrationRequestDto body);
}
