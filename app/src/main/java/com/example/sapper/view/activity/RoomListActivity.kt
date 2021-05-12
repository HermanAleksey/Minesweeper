package com.example.sapper.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bsuir.herman.authscreenapp.IJoinRoomCallback
import com.example.sapper.R
import com.example.sapper.controller.network.NetworkService
import com.example.sapper.controller.network.WebSocketHandler
import com.example.sapper.view.adapter.RoomListAdapter
import com.example.sapper.databinding.ActivityRoomListBinding
import com.example.sapper.model.constant.Constant
import com.example.sapper.model.constant.GameConstant
import com.example.sapper.model.dto.RoomDTO
import com.example.sapper.model.dto.WebGameDto
import com.example.sapper.model.entity.local.MultiplayerGame
import com.example.sapper.view.Utils
import com.example.sapper.view.activity.ChatRoomActivity.ChatRoomActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RoomListActivity : AppCompatActivity(), IJoinRoomCallback {

    lateinit var binding: ActivityRoomListBinding
    lateinit var rooms: ArrayList<RoomDTO>
    val TAG = "TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.onActivityCreateSetTheme(this)
        binding = ActivityRoomListBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        WebSocketHandler.setJoinRoomCallbackListener(this)

        rooms = ArrayList()

        binding.buttonRoomsListRefresh.setOnClickListener {
            fetchData()
        }
        binding.buttonRoomsListAdd.setOnClickListener {
            val intent = Intent(this, GameSettingsActivity::class.java)
            intent.putExtra(Constant().EXTRA_GAME_MODE, Constant().EXTRA_GAME_MODE_INTERNET)
            startActivityForResult(intent, Constant().WEB_GAME_SETTINGS_REQUEST)
        }
    }

    private fun sendRequestCreateRoom(room: WebGameDto) {
        Log.e(TAG, "sendRequestCreateRoom: room:$room")
        NetworkService.getSaperApi().createRoom(room).enqueue(object : Callback<RoomDTO> {
            override fun onResponse(call: Call<RoomDTO>?, response: Response<RoomDTO>?) {
                Toast.makeText(this@RoomListActivity, "onResponse", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<RoomDTO>?, t: Throwable?) {
                Toast.makeText(this@RoomListActivity, "onFailure", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun fetchData() {
        NetworkService.getSaperApi().selectAllRooms().enqueue(object :
            Callback<ArrayList<RoomDTO>> {
            override fun onResponse(
                call: Call<ArrayList<RoomDTO>>?,
                response: Response<ArrayList<RoomDTO>>?
            ) {
                Toast.makeText(this@RoomListActivity, "Rooms list updated", Toast.LENGTH_SHORT)
                    .show()
                rooms = response!!.body()
                Log.e(TAG, "onResponse: $rooms")
                runOnUiThread {
                    updateListView()
                }
            }

            override fun onFailure(call: Call<ArrayList<RoomDTO>>?, t: Throwable?) {
                Toast.makeText(this@RoomListActivity, "Error update list", Toast.LENGTH_SHORT)
                    .show()
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
            intent.putExtra(GameConstant().EXTRA_GAME_OBJECT, room)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return

        val game = data?.getSerializableExtra(GameConstant().EXTRA_GAME_OBJECT) as MultiplayerGame
        sendRequestCreateRoom(
            WebGameDto(
                game.field.width,
                game.field.height,
                game.field.minesCount,
                game.minutes,
                game.seconds,
                game.sameField
            )
        )


    }
}
