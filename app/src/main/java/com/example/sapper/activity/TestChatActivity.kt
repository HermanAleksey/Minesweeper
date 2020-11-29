package com.example.sapper.activity;

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sapper.R
import com.example.sapper.activity.MainActivity.Companion.context
import com.example.sapper.entity.CompanyLevel
import com.example.sapper.logic.MultiPlayerService
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_test_chat.*
import org.json.JSONArray
import org.json.JSONException

class TestChatActivity : AppCompatActivity() {

    // Debugging
    private val TAG = "myLogs"
    private val DIALOG_CHOICE = 1
    private val DIALOG_MINER_LOSS = 2
    private val DIALOG_MINER_WON = 3
    private val DIALOG_BLUETOOTH_MENU = 4

    // Message types sent from the BluetoothChatService Handler
    val MESSAGE_STATE_CHANGE = 1
    val MESSAGE_READ = 2
    val MESSAGE_WRITE = 3
    val MESSAGE_DEVICE_NAME = 4
    val MESSAGE_TOAST = 5
    private val BOARD_POST = 6
    val MESSAGE_SAPPER_LOSS = 7
    val MESSAGE_SAPPER_WIN = 8

    val SOCKET_SERVER = 4
    val SOCKET_CLIENT = 5

    private var socketView = 0

    // Key names received from the BluetoothChatService Handler
    val DEVICE_NAME = "device_name"
    val TOAST = "toast"

    // Intent request codes
    val REQUEST_CONNECT_DEVICE = 2
    private val REQUEST_ENABLE_BT = 3


    lateinit var cellFromBluetooth: Array<IntArray> //An array of field cells obtained by Bluetooth

    // Local Bluetooth adapter
    private var mBluetoothAdapter: BluetoothAdapter? = null

    // Member object for the chat services
    private var mChatService: MultiPlayerService? = null

    /**   -------------------------new constants -------------------------- upon*/

    var string: String = "-------------S T A R T-------------\n"
    var obj = CompanyLevel(1, 10, 10, 16, 10, 0, false)
    var role = ""
    private var GSONobject = ""
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_chat)


        GSONobject = gson.toJson(obj)

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        buttonBTVisibility.setOnClickListener { sendMessages(GSONobject) }
        buttonSend.setOnClickListener { sendMessages(editTextTextPersonName.text.toString()) }

        // Otherwise, setup the chat session
        if (mChatService == null) {
            setupChat()
        }

        when (intent.getStringExtra("Role")) {
            "Server" -> {
                val room = intent.getSerializableExtra("RoomSettings") as CompanyLevel
                role = "Server"
                // Ensure this device is discoverable by others
                ensureDiscoverable()
            }
            "Client" -> {
                // Launch the DeviceListActivity to see devices and do scan
                role = "Client"
                val serverIntent: Intent = Intent(context, DeviceListActivity::class.java)
                startActivityForResult(
                    serverIntent,
                    REQUEST_CONNECT_DEVICE
                )
            }
        }
    }

    /**---------------------------------------------------------------------------------------------------------------------------------------*/

//    @Synchronized
    override fun onResume() {
        super.onResume()
        Log.d("myLogs", "MultiplayerActivity onResume")
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity
        // returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't
            // started already
            if (mChatService!!.state === MultiPlayerService.STATE_NONE) {
                // Start the Bluetooth chat services
                socketView = SOCKET_SERVER
                mChatService!!.start()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CONNECT_DEVICE -> {               // When DeviceListActivity returns with a device to connect
                if (resultCode == RESULT_OK) {
                    connectDeviceFromMenu(data!!)
                }
            }
            REQUEST_ENABLE_BT -> {              // When the request to enable Bluetooth returns
                if (resultCode == RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat()
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(
                        MainActivity.context, "R.string.bt_not_enabled_leaving",
                        Toast.LENGTH_SHORT
                    ).show()

                    /*** Start MainActivity  */
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun ensureDiscoverable() {
        Log.d(TAG, "ensure discoverable")
        if (mBluetoothAdapter!!.scanMode != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            val discoverableIntent = Intent(
                BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE
            )
            discoverableIntent.putExtra(
                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300
            )
            startActivity(discoverableIntent)
        }
    }

    private fun connectDeviceFromMenu(data: Intent) {
        // Get the device MAC address
        val address = data.extras!!.getString(
            DeviceListActivity.EXTRA_DEVICE_ADDRESS
        )
        Log.e(TAG, "connectDeviceFromMenu: Address:${address}")
        // Get the BluetoothDevice object
        try {
            val device: BluetoothDevice = mBluetoothAdapter!!.getRemoteDevice(address)
            // Attempt to connect to the device
            socketView = SOCKET_CLIENT
            mChatService!!.connect(device)
        } catch (e: Exception) {
            Toast.makeText(this, "Error connecting to selected device", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupChat() {
        Log.d(TAG, "setupChat()")
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = MultiPlayerService(this, mHandler)
    }

    // The Handler that gets information back from the BluetoothChatService
    private val mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                BOARD_POST -> {
                }
                MESSAGE_SAPPER_LOSS -> sendMessages("Miner won")
                MESSAGE_SAPPER_WIN -> sendMessages("Sapper won")
                MESSAGE_STATE_CHANGE -> {
                    Log.i(
                        TAG,
                        "MESSAGE_STATE_CHANGE: " + msg.arg1
                    )
                    when (msg.arg1) {
                        MultiPlayerService.STATE_CONNECTED -> if (socketView == SOCKET_CLIENT) {
                            showDialog(DIALOG_CHOICE)
                            Log.d("myLogs", "I am SOCKET_CLIENT")
                        } else if (socketView == SOCKET_SERVER) {
                            Log.d("myLogs", "I am SOCKET_SERVER")
                        }
                        MultiPlayerService.STATE_CONNECTING -> {
                        }
                        MultiPlayerService.STATE_LISTEN -> {
                        }
                        MultiPlayerService.STATE_NONE -> {
                        }
                    }
                }
                MESSAGE_WRITE -> {
                }
                MESSAGE_READ -> {
                    //Read received JSON
                    val readBuf = msg.obj as ByteArray
                    // construct a string from the valid bytes in the buffer
                    /**-----------------------------------------------Message reading --------------------------------------------------------*/
                    val readMessage = String(readBuf, 0, msg.arg1)
                    string += "\n----------new----------\n$readMessage"
                    textViewChat.text = string
                    when {
                        /**-----------------------------------Processing JSON with ROOM configs-----------------------------------------------*/
                        readMessage.startsWith('{') -> {
                            val room = gson.fromJson(readMessage, CompanyLevel::class.java)
                            textViewAppend("Client get and processing room info gson:")
                            textViewAppend(room.toString())

                            /**-----------------Request to start game -------------------*/
                            sendMessages("StartRequest")
                            textViewAppend("Client sande startRequest")
                        }
                        /**------------------------Start request was accepted ON -------------------------*/
                        readMessage == "StartRequest" -> {
                            textViewAppend("Server got start request")
                            sendMessages("AcceptStartRequest")
                            textViewAppend("Server send accept request")
                        }
                        readMessage == "AcceptStartRequest" -> {
                            textViewAppend("Client got answer to start request")
                        }
                        /**-----------Если пришло сообщение "Lose" ------------------*/
                        //тогда настоящий пользователь автоматически победил
                        readMessage == "Lose" -> {
                            Toast.makeText(
                                this@TestChatActivity,
                                "Gratz, u won",
                                Toast.LENGTH_SHORT
                            ).show()
                            textViewAppend("\n\n\nI WON!")
                        }
                        /**-----------Если пришло сообщение "Win" ------------------*/
                        //тогда настоящий пользователь автоматически победил
                        readMessage == "Win" -> {
                            Toast.makeText(
                                this@TestChatActivity,
                                "sorry, u lost",
                                Toast.LENGTH_SHORT
                            ).show()
                            textViewAppend("\n\n\nI LOSE!")
                        }

                    }
                }
                MESSAGE_DEVICE_NAME -> {
                    Toast.makeText(
                        applicationContext,
                        "R.string.connectTo.toString()" + msg.data.getString(DEVICE_NAME),
                        Toast.LENGTH_SHORT
                    ).show()
                    /**--------------------------------------First message after connection -----------------------------------------------**/
                    when (role) {
                        "Client" -> {
                        }
                        "Server" -> {
                            textViewAppend("Server sending room info")
                            //Sending room info as GSON
                            sendMessages(GSONobject)
                        }
                    }
                }
                MESSAGE_TOAST -> {
                    Toast.makeText(
                        applicationContext, msg.data.getString(TOAST), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun textViewAppend(str: String) {
        string += "\n$str\n"
        textViewChat.text = string
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private fun sendMessages(message: String) {
        // Check that we're actually connected before trying anything
        if (mChatService!!.state !== MultiPlayerService.STATE_CONNECTED) {
            Toast.makeText(this, "R.string.not_connected", Toast.LENGTH_SHORT).show()
            return
        }

        // Check that there's actually something to send
        if (message.length > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            val send = message.toByteArray()
            mChatService!!.write(send)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService!!.stop()
        Log.d("myLogs", "MultiplayerActivity onDestroy")
    }

    /**---------------------------------------------------------------------------------------------------------------------------------------*/
}