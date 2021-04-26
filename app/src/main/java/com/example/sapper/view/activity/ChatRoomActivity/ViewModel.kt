package com.example.sapper.view.activity.ChatRoomActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bsuir.herman.authscreenapp.IRoomUpdateCallback
import com.example.sapper.network.WebSocketHandler.setRoomUpdateCallbackListener

object ViewModel : IRoomUpdateCallback{

    init {
        setRoomUpdateCallbackListener(this)
    }

    fun init(){}

    private val _username_1 = MutableLiveData<String>()
    fun uname1(): LiveData<String>{
        return _username_1
    }

    private val _username_2 = MutableLiveData<String>()
    fun uname2(): LiveData<String>{
        return _username_2
    }

    override fun onRoomUpdateReceived(uname1: String, uname2: String) {
        _username_1.postValue(uname1)
        _username_2.postValue(uname2)
    }

}