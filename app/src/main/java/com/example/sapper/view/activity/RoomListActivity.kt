package com.example.sapper.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bsuir.herman.authscreenapp.IJoinRoomCallback
import com.example.sapper.network.NetworkService
import com.example.sapper.network.WebSocketHandler
import com.example.sapper.view.adapter.RoomListAdapter
import com.example.sapper.databinding.ActivityRoomListBinding
import com.example.sapper.model.dto.RoomDTO
import com.example.sapper.view.activity.ChatRoomActivity.ChatRoomActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RoomListActivity : AppCompatActivity(), IJoinRoomCallback {

    lateinit var binding: ActivityRoomListBinding
    lateinit var rooms: ArrayList<RoomDTO>
    val TAG = "RAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomListBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        WebSocketHandler.setJoinRoomCallbackListener(this)

        rooms = ArrayList()

        binding.buttonRoomsListRefresh.setOnClickListener {
            fetchData()
        }
        binding.buttonRoomsListLogout.setOnClickListener {
            finish()
        }
        binding.buttonRoomsListAdd.setOnClickListener {
            NetworkService.getSaperApi().createRoom().enqueue(object : Callback<RoomDTO> {
                override fun onResponse(call: Call<RoomDTO>?, response: Response<RoomDTO>?) {
                    Toast.makeText(this@RoomListActivity, "onResponse", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<RoomDTO>?, t: Throwable?) {
                    Toast.makeText(this@RoomListActivity, "onFailure", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    private fun fetchData() {
        NetworkService.getSaperApi().selectAllRooms().enqueue(object : Callback<ArrayList<RoomDTO>> {
            override fun onResponse(call: Call<ArrayList<RoomDTO>>?, response: Response<ArrayList<RoomDTO>>?) {
                Toast.makeText(this@RoomListActivity, "Rooms list updated", Toast.LENGTH_SHORT).show()
                rooms = response!!.body()
                runOnUiThread {
                    updateListView()
                }
            }

            override fun onFailure(call: Call<ArrayList<RoomDTO>>?, t: Throwable?) {
                Toast.makeText(this@RoomListActivity, "Error update list", Toast.LENGTH_SHORT).show()
            }

        })
    }

    lateinit var room: RoomDTO
    private fun updateListView() {
        val adapter = RoomListAdapter(rooms, applicationContext)

        binding.listViewRoomsList.adapter = adapter
        binding.listViewRoomsList.setOnItemClickListener { parent, view, position, id ->
            room = rooms[position]

            WebSocketHandler.joinRoomRequest(room.id)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        WebSocketHandler.disconnectRequest()
    }

    override fun onJoinRoomResponse(result: Boolean) {
        if (result) {
            val intent = Intent(this, ChatRoomActivity::class.java)
            intent.putExtra("ROOM", room)
            startActivity(intent)
        }
    }

//    override fun onRoomUpdateReceived(uname1: String, uname2: String) {
//        room.player_1.username = uname1
//        room.player_2.username = uname2
//    }
}
