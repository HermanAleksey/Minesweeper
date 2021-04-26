package com.example.sapper.view.activity.ChatRoomActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.bsuir.herman.authscreenapp.IMessageCallback
import com.example.sapper.databinding.ActivityChatRoomBinding
import com.example.sapper.model.dto.RoomDTO
import com.example.sapper.network.WebSocketHandler

class ChatRoomActivity : AppCompatActivity(), IMessageCallback {

    private lateinit var binding: ActivityChatRoomBinding
    lateinit var room: RoomDTO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        ViewModel.uname1().observe(this, observer1)
        ViewModel.uname2().observe(this, observer2)

        WebSocketHandler.setMessageCallbackListener(this)

        room = intent.getSerializableExtra("ROOM") as RoomDTO
        binding.tvChatRoomPlayer1.text = room.player_1.username
        binding.tvChatRoomPlayer2.text = room.player_2.username

        binding.btnChatRoomSend.setOnClickListener {
            sendMessage()
        }
    }

    private val observer1 = Observer<String> {
        room.player_1.username = it
        binding.tvChatRoomPlayer1.text = it
    }
    private val observer2 = Observer<String> {
        room.player_2.username = it
        binding.tvChatRoomPlayer2.text = it
    }

    private fun sendMessage() {
        Log.e("TAG", "sendMessage: sending message (button clicked)")
        WebSocketHandler.writeMessage(room.id, binding.etChatRoomMessage.text.toString())
    }

    companion object{
        const val OPPONENT_WON:String = "OPPONENT_WON"
        const val OPPONENT_LOSE:String = "OPPONENT_LOSE"
        const val OPPONENT_DISCONNECTED:String = "OPPONENT_DISCONNECTED"
    }

    override fun onMessageReceived(from: String, context: String) {
        Log.e("TAG", "onMessageReceived: callback on activity received.\ncontext:$context")

        when (context){
            OPPONENT_WON -> {
                makeToast(OPPONENT_WON)
                return
            }
            OPPONENT_DISCONNECTED -> {
                makeToast(OPPONENT_DISCONNECTED)
                return
            }
            else -> {
                concatMessage("From:$from\n$context\n")
                return
            }
        }
    }

    private fun makeToast(string: String){
        runOnUiThread {
            Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
        }
    }

    private fun concatMessage(string: String) {
        Log.e("TAG", "concatMessage:")
        runOnUiThread {
            binding.tvChatRoomChat.text = binding.tvChatRoomChat.text.toString() + string + "\n"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        WebSocketHandler.leaveRoomRequest(room.id)
    }
}