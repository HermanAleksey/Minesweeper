package com.example.sapper.model.entity.web

import com.example.sapper.model.dto.LoginResponseDto

class WebPlayer{
    var userId: Long
    var username:String

    constructor(id: Long, username: String){
        this.userId = id
        this.username = username
    }

    constructor(loginResponseDto: LoginResponseDto) {
        this.userId = loginResponseDto.userId
        this.username = loginResponseDto.username
    }


}
