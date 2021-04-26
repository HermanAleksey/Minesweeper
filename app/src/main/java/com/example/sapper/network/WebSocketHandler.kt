package com.example.sapper.network

import android.util.Log
import android.widget.Toast
import com.bsuir.herman.authscreenapp.IJoinRoomCallback
import com.bsuir.herman.authscreenapp.IMessageCallback
import com.bsuir.herman.authscreenapp.IRoomUpdateCallback
import com.example.sapper.model.entity.web.WebPlayer
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.lang.Exception

object WebSocketHandler : IMessageCallback, IJoinRoomCallback, IRoomUpdateCallback {

    private const val TAG = "TAG"
    private const val URL: String = "wss://192.168.1.7:8080/websocket"

    //    private const val URL: String = "wss://26.36.145.54:8080/websocket"
    private var currentRoomId: Int = 0
    var webPlayer: WebPlayer? = null

    public fun openWSConnection(webPlayer: WebPlayer) {
//        try to handshake wss connection
        try {
            initialize()
            this.webPlayer = webPlayer
            openedConnection()
        } catch (e: Exception){
            Log.e(TAG, "openWSConnection: Error occured while opening wss connection", )
        }

    }

    private lateinit var messageCallbackListener: IMessageCallback
    fun setMessageCallbackListener(callback: IMessageCallback) {
        this.messageCallbackListener = callback
    }

    private lateinit var joinRoomCallbackListener: IJoinRoomCallback
    fun setJoinRoomCallbackListener(callback: IJoinRoomCallback) {
        this.joinRoomCallbackListener = callback
    }

    private lateinit var roomUpdateCallbackListener: IRoomUpdateCallback
    fun setRoomUpdateCallbackListener(callback: IRoomUpdateCallback) {
        this.roomUpdateCallbackListener = callback
    }

    var webSocket: WebSocket? = null

    fun initialize() {
        Log.e(TAG, "initialize")

        val request = Request.Builder()
            .url(URL)
            .build()
        val listener = MyWebSocketListener()
        webSocket = NetworkService.getUnsafeOkHttpClient()?.newWebSocket(request, listener)
    }

    init {
        Log.e(TAG, "init")
    }

    private fun sendWSMessage(string: String): Boolean {
        Log.e("TAG_WSS", "WSS message send: $string")
        if (webPlayer == null) return false
        webSocket?.send(string)
        return true
    }

    fun openedConnection(): Boolean {
        return sendWSMessage("$ON_CONNECTED;${webPlayer!!.userId}")
    }

    fun joinRoomRequest(roomId: Int): Boolean {
        return sendWSMessage("$JOIN_REQUEST;${webPlayer!!.userId};$roomId")
    }

    fun leaveRoomRequest(roomId: Int): Boolean {
        return sendWSMessage("$LEFT_ROOM_REQUEST;${webPlayer!!.userId};$roomId")
    }

    fun writeMessage(roomId: Int, msg: String?): Boolean {
        return sendWSMessage("$SEND_ROOM_MESSAGE;${webPlayer!!.userId};$roomId;$msg")
    }

    fun disconnectRequest(): Boolean {
        val res = sendWSMessage("$DISCONNECT_REQUEST;${webPlayer!!.userId}")
        webPlayer = null
        return res
    }

    //For sending
    private const val ON_CONNECTED = "ON_CONNECTED"
    private const val JOIN_REQUEST = "JOIN_REQUEST"
    private const val LEFT_ROOM_REQUEST = "LEFT_ROOM_REQUEST"
    private const val SEND_ROOM_MESSAGE = "SEND_ROOM_MESSAGE"
    private const val DISCONNECT_REQUEST = "DISCONNECT_REQUEST"

    //fFor receiving
    private const val JOIN_RESPONSE = "JOIN_RESPONSE"
    private const val MESSAGE_RESPONSE = "MESSAGE_RESPONSE"
    private const val NOTIFY_ROOM_UPDATE = "NOTIFY_ROOM_UPDATE"

    private class MyWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.e(TAG, "onMessage: $text")
            val array = text.split(";")
            when (array[0]) {
                MESSAGE_RESPONSE -> {
                    val from = array[1].removePrefix("From:")
                    val context = array[2].removePrefix("Message:")
                    onMessageReceived(from,context)
                    return
                }
                JOIN_RESPONSE -> {
                    val result = java.lang.Boolean.parseBoolean(array[1])
                    onJoinRoomResponse(result)
                    return
                }
                NOTIFY_ROOM_UPDATE -> {
                    val uname1 = array[1]
                    val uname2 = array[2]
                    onRoomUpdateReceived(uname1, uname2)
                    return
                }
                else -> {
                }
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null)
            webPlayer = null
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "onFailure: " + t.message)
        }

        companion object {
            private const val NORMAL_CLOSURE_STATUS = 1000
        }
    }

    override fun onMessageReceived(from: String, context: String) {
        Log.e(TAG, "onMessageReceived: sending callback from WebPlayer")
        messageCallbackListener.onMessageReceived(from, context)
    }

    override fun onRoomUpdateReceived(uname1: String, uname2: String) {
        Log.e(TAG, "onRoomUpdateReceived: updating room info")
        roomUpdateCallbackListener.onRoomUpdateReceived(uname1, uname2)
    }

    override fun onJoinRoomResponse(result: Boolean) {
        Log.e(TAG, "onJoinRoomResponse: room join result:$result")
        joinRoomCallbackListener.onJoinRoomResponse(result)
    }


}