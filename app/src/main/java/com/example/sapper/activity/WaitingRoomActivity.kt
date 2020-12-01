package com.example.sapper.activity

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import com.example.sapper.constant.Constant
import com.example.sapper.constant.GameConstant
import com.example.sapper.R
import com.example.sapper.activity.MinefieldActivity.activity.MinefieldActivity
import kotlinx.android.synthetic.main.activity_waiting_room.*
import kotlinx.android.synthetic.main.activity_game_settings.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class WaitingRoomActivity : AppCompatActivity() {

    lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothDevices = mutableListOf<Any>()

    lateinit var sendReceive: SendReceive

    val STATE_LISTENING: Int = 1
    val STATE_CONNECTING: Int = 2
    val STATE_CONNECTED: Int = 3
    val STATE_CONNECTION_FAILED: Int = 4
    val STATE_MESSAGE_RECIEVED: Int = 5

    val REQUEST_ENABLE_BLUETOOTH: Int = 1

    private val APP_NAME = "BTChat"
    private val MY_UUID: UUID = UUID
        .fromString("8ce255c0-223a-11e0-ac64-0803450c9a66")
    private val TAG = "BLEApp"

    lateinit var role: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_room)

        implementListeners()
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!bluetoothAdapter.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        role = intent.getStringExtra(Constant().EXTRA_BLUETOOTH_ROLE)!!
        when (role) {
            Constant().ROLE_SERVER -> {
                ll_client_waiting_room.visibility = View.GONE
                //fill info about game
                fillServerRoomInfoFromIntent()

                val serverClass = ServerClass()
                serverClass.start()
            }
            Constant().ROLE_CLIENT -> {
                ll_host_waiting_room.visibility = View.GONE
                btn_client_room_blu_search_update.callOnClick()
            }
        }

    }

    private fun fillServerRoomInfoFromIntent() {
        val gameMode = when (intent.getStringExtra(Constant().EXTRA_GAME_MODE)) {
            Constant().EXTRA_GAME_MODE_BLUETOOTH -> getString(R.string.gameModeBluetooth)
            Constant().EXTRA_GAME_MODE_COMPANY -> getString(R.string.gameModeCompany)
            Constant().EXTRA_GAME_MODE_CREATIVE -> getString(R.string.gameModeCasual)
            else -> ""
        }
        val width = intent.getIntExtra(GameConstant().WIDTH_TAG, 0)
        val height = intent.getIntExtra(GameConstant().HEIGHT_TAG, 0)
        val minesCount = intent.getIntExtra(GameConstant().MINES_COUNT_TAG, 0)
        val gameTime = intent.getStringExtra(GameConstant().GAME_TIME_TAG)
        val firstClickMine = intent.getBooleanExtra(GameConstant().FIRST_CLICK_MINE_TAG, false)
        val exitOnLose = intent.getBooleanExtra(GameConstant().CLOSE_AFTER_GAME_TAG, false)
        val useSameField = intent.getBooleanExtra(GameConstant().USE_SAME_FIELD_TAG, false)

        tv_host_room_game_mode.text = gameMode
        tv_host_room_field_height.text = height.toString()
        tv_host_room_field_width.text = width.toString()
        tv_host_room_mines_amount.text = minesCount.toString()
        tv_host_room_time_limit.text = gameTime
        chb_host_room_first_click.isChecked = firstClickMine
        cb_host_room_exit.isChecked = exitOnLose
        cb_host_room_use_same_field.isChecked = useSameField

        tv_host_room_player_1_name.text = bluetoothAdapter.name
    }

    private fun sendMessageToConnectedDevice(str: String) {
        sendReceive.write(str.toByteArray())
    }

    private fun implementListeners() {
        //client LL
        btn_client_room_blu_search_update.setOnClickListener {
            val bt: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
            Log.e("TAG", "implementListeners: $bt")
            val strings = mutableListOf<String>()

            var index = 0

            if (bt.isNotEmpty()) {
                bt.forEach {
                    bluetoothDevices.add(it)
                    strings.add(index, it.name)
                    index++
                }
                val arrayAdapter: ArrayAdapter<String> =
                    ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, strings)
                lv_client_room_connected_dev.adapter = arrayAdapter
            }
        }
        lv_client_room_connected_dev.setOnItemClickListener { parent, view, position, id ->
            val clientClass = ClientClass(bluetoothDevices[position] as BluetoothDevice)
            clientClass.start()
            title = "Connecting"
        }
        //host LL
        btn_host_room_start_game.setOnClickListener {
            sendMessageToConnectedDevice("start game")
//            val minefieldIntent = Intent(this,MinefieldActivity::class.java)
//            minefieldIntent.putExtra(Constant().GAME_MODE, Constant().GAME_MODE_BLUETOOTH)
//            minefieldIntent.putExtra(GameConstant().WIDTH_TAG,tv_host_room_field_width.text.toString().toInt())
//            minefieldIntent.putExtra(GameConstant().HEIGHT_TAG,tv_host_room_field_height.text.toString().toInt())
//            minefieldIntent.putExtra(GameConstant().MINES_COUNT_TAG,tv_host_room_mines_amount.text.toString().toInt())
//            minefieldIntent.putExtra(GameConstant().GAME_TIME_TAG,tv_host_room_time_limit.text.toString())
//            minefieldIntent.putExtra(GameConstant().USE_SAME_FIELD_TAG,cb_host_room_use_same_field.isChecked)
//            minefieldIntent.putExtra(GameConstant().FIRST_CLICK_MINE_TAG,cb_game_settings_first_click_mine.isChecked)
//            startActivity(minefieldIntent)
        }
    }

    val handler: Handler = Handler {
        when (it.what) {
            STATE_LISTENING -> {
                title = "Listening"
            }
            STATE_CONNECTING -> {
                title = "Connecting"
            }
            STATE_CONNECTED -> {
                title = "Connected"
                if (role == Constant().ROLE_CLIENT) {
                    //make LL of room info visible
                    ll_host_waiting_room.visibility = View.VISIBLE
                    ll_client_waiting_room.visibility = View.GONE
                    btn_host_room_start_game.visibility = View.GONE
                    //setup nickname of client
                    tv_host_room_player_2_name.text = bluetoothAdapter.name
                    //now possible to start game
                    btn_host_room_start_game.isClickable = true

                    sendMessageToConnectedDevice("initial")
                    //sending username
                    sendMessageToConnectedDevice("ClientName:${bluetoothAdapter.name};")
                }
                if (role == Constant().ROLE_SERVER) {
                    sendMessageToConnectedDevice("initial")
                    //sending username
                    sendMessageToConnectedDevice("ServerName:${bluetoothAdapter.name};")
                    //server sending info about room
                    sendMessageToConnectedDevice("ServerRoomMode:${tv_host_room_game_mode.text};")
                    sendMessageToConnectedDevice("ServerRoomHeight:${tv_host_room_field_height.text};")
                    sendMessageToConnectedDevice("ServerRoomWidth:${tv_host_room_field_width.text};")
                    sendMessageToConnectedDevice("ServerRoomMinesCount:${tv_host_room_mines_amount.text};")
                    sendMessageToConnectedDevice("ServerRoomTimeLimit:${tv_host_room_time_limit.text};")
                    sendMessageToConnectedDevice("ServerRoomFirstClick:${chb_host_room_first_click.isChecked};")
                    sendMessageToConnectedDevice("ServerRoomSameField:${cb_host_room_use_same_field.isChecked};")
                }
            }
            STATE_CONNECTION_FAILED -> {
                title = "Connection failed"
            }
            STATE_MESSAGE_RECIEVED -> {
                val readBuffer: ByteArray = it.obj as ByteArray
                val tempMessage = String(readBuffer, 0, it.arg1)

                if (role == Constant().ROLE_SERVER) {
                    if (tempMessage.startsWith("initial")) {
                        //processing client nickname from Client
                        tv_host_room_player_2_name.text =
                            tempMessage.substringAfter("ClientName:").substringBefore(";")
                    }
                    if (tempMessage.startsWith("endGameClient")){
                        MinefieldActivity().finish()
                    }
                }
                if (role == Constant().ROLE_CLIENT) {
                    if (tempMessage.startsWith("initial")) {
                        tv_host_room_player_1_name.text =
                            tempMessage.substringAfter("ServerName:").substringBefore(";")
                        tv_host_room_game_mode.text =
                            tempMessage.substringAfter("ServerRoomMode:").substringBefore(";")
                        tv_host_room_field_height.text =
                            tempMessage.substringAfter("ServerRoomHeight:").substringBefore(";")
                        tv_host_room_field_width.text =
                            tempMessage.substringAfter("ServerRoomWidth:").substringBefore(";")
                        tv_host_room_mines_amount.text =
                            tempMessage.substringAfter("ServerRoomMinesCount:").substringBefore(";")
                        tv_host_room_time_limit.text =
                            tempMessage.substringAfter("ServerRoomTimeLimit:").substringBefore(";")

                        chb_host_room_first_click.isChecked =
                            tempMessage.substringAfter("ServerRoomFirstClick:")
                                .substringBefore(";") == "true"
                        cb_host_room_exit.isChecked =
                            tempMessage.substringAfter("ServerRoomLeaveAfter:")
                                .substringBefore(";") == "true"
                        cb_host_room_use_same_field.isChecked =
                            tempMessage.substringAfter("ServerRoomSameField:")
                                .substringBefore(";") == "true"
                    } else
                    if (tempMessage.startsWith("start game")){
                        val minefieldIntent = Intent(this, MinefieldActivity::class.java)
                        minefieldIntent.putExtra(Constant().EXTRA_GAME_MODE, Constant().EXTRA_GAME_MODE_BLUETOOTH)
                        minefieldIntent.putExtra(GameConstant().WIDTH_TAG,tv_host_room_field_width.text.toString().toInt())
                        minefieldIntent.putExtra(GameConstant().HEIGHT_TAG,tv_host_room_field_height.text.toString().toInt())
                        minefieldIntent.putExtra(GameConstant().MINES_COUNT_TAG,tv_host_room_mines_amount.text.toString().toInt())
                        minefieldIntent.putExtra(GameConstant().GAME_TIME_TAG,tv_host_room_time_limit.text.toString())
                        minefieldIntent.putExtra(GameConstant().USE_SAME_FIELD_TAG,cb_host_room_use_same_field.isChecked)
                        minefieldIntent.putExtra(GameConstant().CLOSE_AFTER_GAME_TAG,cb_game_settings_exit.isChecked)
//                        minefieldIntent.putExtra(GameConstant().FIRST_CLICK_MINE_TAG,cb_game_settings_first_click_mine.isChecked)
                        startActivity(minefieldIntent)
                    }
                    if (tempMessage.startsWith("endGameServer")){
                        MinefieldActivity().finish()
                    }
                }
            }
        }
        return@Handler true
    }

    inner class ServerClass : Thread() {
        private lateinit var bluetoothServerSocket: BluetoothServerSocket

        init {
            try {
                bluetoothServerSocket = bluetoothAdapter
                    .listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID)
                Log.d(TAG, "ServerThread: Setting up using: $MY_UUID ")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun run() {
            var bluetoothSocket: BluetoothSocket? = null
            while (bluetoothSocket == null) {
                try {
                    Log.d(TAG, "run: Server socket start.")
                    val message: Message = Message.obtain()
                    message.what = STATE_CONNECTING
                    handler.sendMessage(message)

                    /*code will not reach this point until connection not will be accepted*/
                    bluetoothSocket = bluetoothServerSocket.accept()
                    Log.d(TAG, "run: Server socket accepted connection.")
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e(TAG, "run: Server socket failed to accept connection. ${e.message}")
                    val message: Message = Message.obtain()
                    message.what = STATE_CONNECTION_FAILED
                    handler.sendMessage(message)
                }

                if (bluetoothSocket != null) {
                    val message: Message = Message.obtain()
                    message.what = STATE_CONNECTED
                    handler.sendMessage(message)

                    sendReceive = SendReceive(bluetoothSocket)
                    sendReceive.start()
                    Log.d("TAG", "run: SendReceive initialized ---------------")
                    break
                }
                Log.d(TAG, "run: End of ServerThread ")
            }
        }

        fun cancel() {
            Log.d(TAG, "cancel: Canceling AcceptThread.")
            try {
                bluetoothServerSocket.close()
            } catch (e: IOException) {
                Log.e(TAG, "cancel: Close of ServerThread ServerSocket failed. ${e.message}")
            }
        }
    }

    inner class ClientClass(device: BluetoothDevice) : Thread() {
        private lateinit var socket: BluetoothSocket

        init {
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun run() {
            try {
                socket.connect()
                val message = Message.obtain()
                message.what = STATE_CONNECTED
                handler.sendMessage(message)

                sendReceive = SendReceive(socket)
                sendReceive.start()
            } catch (e: IOException) {
                e.printStackTrace()
                val message = Message.obtain()
                message.what = STATE_CONNECTION_FAILED
                handler.sendMessage(message)
            }
        }
    }

    inner class SendReceive(socket: BluetoothSocket) : Thread() {
        var bluetoothSocket: BluetoothSocket = socket
        var inputStream: InputStream
        var outputStream: OutputStream

        init {
            var tempIn: InputStream? = null
            var tempOut: OutputStream? = null

            try {
                tempIn = bluetoothSocket.inputStream
                tempOut = bluetoothSocket.outputStream
            } catch (e: IOException) {
                e.printStackTrace()
            }

            inputStream = tempIn!!
            outputStream = tempOut!!
        }

        override fun run() {
            val buffer = ByteArray(1024)
            var bytes: Int

            while (true) {
                try {
                    bytes = inputStream.read(buffer)
                    handler.obtainMessage(STATE_MESSAGE_RECIEVED, bytes, -1, buffer).sendToTarget()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        fun write(bytes: ByteArray) {
            try {
                outputStream.write(bytes)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}