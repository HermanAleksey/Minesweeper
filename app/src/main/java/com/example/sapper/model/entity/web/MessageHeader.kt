package com.example.sapper.model.entity.web

enum class MessageHeader(val str:String) {
    //for sending
    ON_CONNECTED("ON_CONNECTED"),
    JOIN_REQUEST("JOIN_REQUEST"),
    LEFT_ROOM_REQUEST("LEFT_ROOM_REQUEST"),
    SEND_ROOM_MESSAGE("SEND_ROOM_MESSAGE"),
    DISCONNECT_REQUEST("DISCONNECT_REQUEST"),

    //For receiving
    JOIN_RESPONSE("JOIN_RESPONSE"),
    MESSAGE_RESPONSE("MESSAGE_RESPONSE"),
    NOTIFY_ROOM_UPDATE("NOTIFY_ROOM_UPDATE");
}