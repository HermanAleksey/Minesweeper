package com.example.sapper.activity.MinefieldActivity.activity;

import HostField
import Saper
import UserField
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.media.SoundPool
import android.os.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sapper.R
import com.example.sapper.activity.DeviceListActivity
import com.example.sapper.activity.GameResultsActivity
import com.example.sapper.activity.MainActivity
import com.example.sapper.activity.MainActivity.Companion.context
import com.example.sapper.activity.MinefieldActivity.IMinefieldActivity
import com.example.sapper.constant.BluetoothConstant
import com.example.sapper.constant.Constant
import com.example.sapper.constant.GameConstant
import com.example.sapper.entity.Room
import com.example.sapper.logic.MinefieldAdapter
import com.example.sapper.logic.MultiPlayerService
import com.example.sapper.logic.SoundPoolWorker
import com.example.sapper.logic.TimeWorker
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_minefield.*
import kotlinx.android.synthetic.main.activity_minefield_bt.*

class MinefieldBTActivity : AppCompatActivity(), IMinefieldActivity {
    private var socketView = 0

    // Local Bluetooth adapter
    private var mBluetoothAdapter: BluetoothAdapter? = null

    // Member object for the chat services
    private var mChatService: MultiPlayerService? = null

    /**   -------------------------new constants -------------------------- upon*/

    var string: String = "-------------S T A R T-------------\n"
    var role = ""
    private var GSONobject = ""
    private val gson = Gson()

    lateinit var roomSettings: Room
    lateinit var arrayButtonsField: Array<Array<Button>>
    lateinit var hostField: HostField
    lateinit var userField: UserField

    /** ---------------------------------SOUND POOL IMPL -----------------------------------**/
    private lateinit var soundPoolWorker: SoundPoolWorker

    private lateinit var soundPool: SoundPool
    private var soundExplosion: Int = 0
    private var soundWin: Int = 0
    private var soundFlagDrop: Int = 0
    private var soundTap: Int = 0

    /**---------------------------------TIMER + STOP WATCH ---------------------------------**/
    private lateinit var timeWorker: TimeWorker
    private var countDownTimer: CountDownTimer? = null
    private lateinit var handler: Handler

    private var gameTimerMilli: Long = 0
//    private var startTime: Long = 0
    lateinit var stopWatchRunnable: Runnable


    private fun startStopWatch() {
        /*stopwatch / timer starting*/
        gameTimerMilli = timeWorker.translateToMilli("${roomSettings.minutes}:${roomSettings.seconds}")
        if (gameTimerMilli == 0L) {
            val startTime = SystemClock.uptimeMillis()
            stopWatchRunnable = timeWorker.getStopWatchRunnable(startTime)
            stopWatchRunnable.run()
        } else {
            timeWorker.getCountDownTimer(
                tv_bt_minefield_minutes,
                tv_bt_minefield_seconds,
                gameTimerMilli
            ).start()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (ll_bt_minefield_minefield_screen.visibility == View.VISIBLE) {
            ll_bt_minefield_minefield_screen.visibility = View.GONE
        } else ll_bt_minefield_minefield_screen.visibility = View.VISIBLE
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minefield_bt)
        //timer
        handler = Handler()
        timeWorker = TimeWorker(this, handler)

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        buttonBTVisibility.setOnClickListener { sendMessages(GSONobject) }
        buttonSend.setOnClickListener { sendMessages(editTextTextPersonName.text.toString()) }

        // setup the chat session
        if (mChatService == null) {
            setupChat()
        }

        //configure sound pool
        soundPoolWorker = SoundPoolWorker()
        soundPool = soundPoolWorker.getSoundPool()
        loadSounds()

        when (intent.getStringExtra(Constant().EXTRA_BLUETOOTH_ROLE)) {
            Constant().ROLE_SERVER -> {
                roomSettings = intent.getSerializableExtra(GameConstant().EXTRA_ROOM) as Room
                GSONobject = gson.toJson(roomSettings)
                role = "Server"

                // Ensure this device is discoverable by others
                ensureDiscoverable()
                fillViewsWithValues(
                    roomSettings.width,
                    roomSettings.height,
                    roomSettings.minutes,
                    roomSettings.seconds,
                    roomSettings.minutes
                )
            }
            Constant().ROLE_CLIENT -> {
                // Launch the DeviceListActivity to see devices and do scan
                role = "Client"
                val serverIntent = Intent(context, DeviceListActivity::class.java)
                startActivityForResult(
                    serverIntent,
                    BluetoothConstant.REQUEST_CONNECT_DEVICE
                )
            }
        }
    }

    private fun configureSeekBar(arrayButtonsField: Array<Array<Button>>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seek_bar_bt_minefield_cell_size.min = 30
        }
        seek_bar_bt_minefield_cell_size.max = 200
        seek_bar_bt_minefield_cell_size.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                arrayButtonsField.forEach {
                    it.forEach { btn ->
                        val params = btn.layoutParams
                        params.width = progress
                        params.height = progress
                        btn.layoutParams = params
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        seek_bar_bt_minefield_cell_size.setProgress(80)
    }

    private fun setOnClickListenerForField(
        arrayButtonsField: Array<Array<Button>>,
        userField: Array<Array<Char>>
    ) {
        for (y in arrayButtonsField.indices) {
            for (x in arrayButtonsField[y].indices) {
                arrayButtonsField[x][y].setOnClickListener {
                    /*Opener*/
                    if (!toggle_button_bt_minefield_flag.isChecked) {
                        soundPoolWorker.playSound(soundPool, soundTap)
                        val keepGame = Saper().openCoordinate(x, y, hostField.content, userField)
                        if (!keepGame) {
                            performEndEvents(false)
                        } else {
                            /*если не проиграл - проверить, возможно теперь условия выполняются.*/
                            /*т.к. openCoordinate возвращает false только если проиграл и не отличает
                            * продолжение игры от победы*/
                            if (Saper().checkWinCondition(hostField.content, userField)) {
                                performEndEvents(true)
                            }
                        }
                        MinefieldAdapter().setupMinefield(userField, arrayButtonsField)
                    }
                    /*FLAGer*/
                    if (toggle_button_bt_minefield_flag.isChecked) {
                        soundPoolWorker.playSound(soundPool, soundFlagDrop)
                        val win = Saper().useFlagOnSpot(x, y, hostField.content, userField)
                        MinefieldAdapter().setupMinefield(userField, arrayButtonsField)
                        if (win) {
                            performEndEvents(true)
                        }
                    }
                }
            }
        }
    }

    private fun prepareFields() {
        hostField = HostField(
            roomSettings.width,
            roomSettings.height,
            roomSettings.minesCount
        )
        arrayButtonsField =
            MinefieldAdapter().createMinefield(
                roomSettings.width, roomSettings.height,
                ll_bt_minefield_minefield_layout, this@MinefieldBTActivity
            )
        configureSeekBar(arrayButtonsField)
        userField = UserField(
            roomSettings.width,
            roomSettings.height,
            roomSettings.minesCount
        )
        setOnClickListenerForField(
            arrayButtonsField,
            userField.content
        )
        MinefieldAdapter().setupMinefield(userField.content, arrayButtonsField)

        fillViewsWithValues(
            roomSettings.width,
            roomSettings.height,
            roomSettings.minutes,
            roomSettings.seconds,
            roomSettings.minutes
        )

        startStopWatch()
    }

    private fun fillViewsWithValues(
        width: Int, height: Int, min: Int,
        sec: Int, mines: Int
    ) {
        tv_bt_minefield_field_width.text = width.toString()
        tv_bt_minefield_field_height.text = height.toString()
        tv_bt_minefield_minutes.text = if (min < 10) {
            "0$min"
        } else "$min"
        tv_bt_minefield_seconds.text = if (sec < 10) {
            "0$sec"
        } else "$sec"
        tv_bt_minefield_mines.text = mines.toString()
    }

    private fun performEndEvents(result: Boolean) {
        val sound = if (result) soundWin else soundExplosion
        soundPoolWorker.playSound(soundPool, sound)
        ll_bt_minefield_minefield_layout.postDelayed({
                intentToResultActivity(result)
            timeWorker.stopCountDownTimer(countDownTimer)
            sendMessages(if (result) "Lose" else "Win")
            disconnectBluetooth()
        }, 500)
    }


    private fun loadSounds() {
        soundExplosion = soundPool.load(this, R.raw.explosion_8bit, 1)
        soundWin = soundPool.load(this, R.raw.win_01, 1)
        soundFlagDrop = soundPool.load(this, R.raw.flag_drop, 1)
        soundTap = soundPool.load(this, R.raw.new_tap, 1)
    }

    override fun intentToResultActivity(result: Boolean) {
        val mIntent = Intent(this, GameResultsActivity::class.java)

        mIntent.putExtra(
            GameConstant().GAME_RESULT,
            result
        )
        mIntent.putExtra(
            GameConstant().EXTRA_WIDTH,
            tv_bt_minefield_field_width.text.toString().toInt()
        )
        mIntent.putExtra(
            GameConstant().EXTRA_HEIGHT,
            tv_bt_minefield_field_height.text.toString().toInt()
        )
        mIntent.putExtra(
            GameConstant().EXTRA_MINES_COUNT,
            tv_bt_minefield_mines.text.toString().toInt()
        )
        if (gameTimerMilli == 0L) {
            mIntent.putExtra(
                GameConstant().EXTRA_GAME_TIME_MINUTES,
                tv_bt_minefield_minutes.text.toString().toInt()
            )
            mIntent.putExtra(
                GameConstant().EXTRA_GAME_TIME_SECONDS,
                tv_bt_minefield_seconds.text.toString().toInt()
            )
        } else {
            var seconds = roomSettings.seconds - tv_bt_minefield_seconds.text.toString().toInt()
            var minutes = roomSettings.minutes - tv_bt_minefield_minutes.text.toString().toInt()
            if (seconds < 0) {
                seconds += 60
                minutes--
            }
            mIntent.putExtra(
                GameConstant().EXTRA_GAME_TIME_SECONDS,
                seconds
            )
            mIntent.putExtra(
                GameConstant().EXTRA_GAME_TIME_MINUTES,
                minutes
            )
        }
        startActivity(mIntent)
        finish()
    }

    override fun textViewMinutesSetText(str: String) {
        tv_bt_minefield_minutes.text = str
    }

    override fun textViewSecondsSetText(str: String) {
        tv_bt_minefield_seconds.text = str
    }

    /**---------------------------------------------------------------------------------------------------------------------------------------*/
    private fun disconnectBluetooth() {
        if (mChatService != null) mChatService!!.stop()
    }

    @Synchronized
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
                socketView = BluetoothConstant.SOCKET_SERVER
                mChatService!!.start()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            BluetoothConstant.REQUEST_CONNECT_DEVICE -> {               // When DeviceListActivity returns with a device to connect
                if (resultCode == RESULT_OK) {
                    connectDeviceFromMenu(data!!)
                }
            }
            BluetoothConstant.REQUEST_ENABLE_BT -> {              // When the request to enable Bluetooth returns
                if (resultCode == RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat()
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(
                        context, R.string.btNotAvailable,
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
        Log.d(BluetoothConstant.TAG, "ensure discoverable")
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
        Log.e(BluetoothConstant.TAG, "connectDeviceFromMenu: Address:${address}")
        // Get the BluetoothDevice object
        try {
            val device: BluetoothDevice = mBluetoothAdapter!!.getRemoteDevice(address)
            // Attempt to connect to the device
            socketView = BluetoothConstant.SOCKET_CLIENT
            mChatService!!.connect(device)
        } catch (e: Exception) {
            Toast.makeText(this, "Error connecting to selected device", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupChat() {
        Log.d(BluetoothConstant.TAG, "setupChat()")
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = MultiPlayerService(this, mHandler)
    }

    // The Handler that gets information back from the BluetoothChatService
    private val mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                BluetoothConstant.BOARD_POST -> {
                }
                BluetoothConstant.MESSAGE_STATE_CHANGE -> {
                    when (msg.arg1) {
                        MultiPlayerService.STATE_CONNECTED -> if (socketView == BluetoothConstant.SOCKET_CLIENT) {
                            showDialog(BluetoothConstant.DIALOG_CHOICE)
                            Log.d("myLogs", "I am SOCKET_CLIENT")
                        } else if (socketView == BluetoothConstant.SOCKET_SERVER) {
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
                BluetoothConstant.MESSAGE_WRITE -> {
                }
                BluetoothConstant.MESSAGE_READ -> {
                    //Read received JSON
                    val readBuf = msg.obj as ByteArray
                    // construct a string from the valid bytes in the buffer
                    /**-----------------------------------------------Message reading --------------------------------------------------------*/
                    val readMessage = String(readBuf, 0, msg.arg1)
                    textViewAppend(
                        """On BluetoothConstant.MESSAGE_READ
                        current read buffer state is: 
                        $readBuf
                        
                        current read message state is: 
                        $readMessage
                    """.trimMargin()
                    )
                    when {
                        /**-----------------------------------Processing JSON with ROOM configs-----------------------------------------------*/
                        readMessage.startsWith('{') -> {
                            roomSettings = gson.fromJson(readMessage, Room::class.java)
                            textViewAppend("Client get and processing room info gson:")
                            textViewAppend(roomSettings.toString())

                            prepareFields()

                            tv_bt_minefield_accepted_object.text = roomSettings.toString()

                            /**-----------------Request to start game -------------------*/
                            sendMessages("StartRequest")
                            textViewAppend("Client sande startRequest")
                        }
                        /**------------------------Start request was accepted ON -------------------------*/
                        readMessage == "StartRequest" -> {
                            sendMessages("AcceptStartRequest")
                            textViewAppend("Server got start request & send accept request")
                        }
                        readMessage == "AcceptStartRequest" -> {
                            textViewAppend("Client got answer to start request")
                        }
                        /**-----------Если пришло сообщение "Lose" ------------------*/
                        readMessage == "Lose" -> {
                            Toast.makeText(
                                this@MinefieldBTActivity,
                                R.string.opponentWon,
                                Toast.LENGTH_SHORT
                            ).show()
                            disconnectBluetooth()
                            performEndEvents(false)
                        }
                        /**-----------Если пришло сообщение "Win" ------------------*/
                        readMessage == "Win" -> {
                            Toast.makeText(
                                this@MinefieldBTActivity,
                                R.string.opponentExploded,
                                Toast.LENGTH_SHORT
                            ).show()
                            disconnectBluetooth()
                            performEndEvents(true)
                        }
                        else -> {
                            textViewAppend("Incoming message:\n\"$readMessage\"\ncan't be processed")
                        }

                    }
                }
                BluetoothConstant.MESSAGE_DEVICE_NAME -> {
                    Toast.makeText(
                        applicationContext,
                        R.string.connectedDevice.toString() + msg.data.getString(BluetoothConstant.DEVICE_NAME),
                        Toast.LENGTH_SHORT
                    ).show()
                    /**--------------------------------------First message after connection -----------------------------------------------**/
                    when (role) {
                        "Client" -> {
                        }
                        "Server" -> {
                            textViewAppend("Server sending room info")
                            tv_bt_minefield_gson_sended.text = GSONobject
                            //Sending room info as GSON
                            sendMessages(GSONobject)

                            prepareFields()
                        }
                    }
                }
                BluetoothConstant.MESSAGE_TOAST -> {
                    Toast.makeText(
                        applicationContext,
                        msg.data.getString(BluetoothConstant.TOAST),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun textViewAppend(str: String) {
        string += "\n-----------------------------\n$str\n"
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
            textViewAppend("Not connected to any device. Can't send msg")
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
        timeWorker.stopCountDownTimer(countDownTimer)
        soundPool.release()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (mChatService != null) mChatService!!.stop()
        timeWorker.stopCountDownTimer(countDownTimer)
        soundPool.release()
        finish()
    }

    /**---------------------------------------------------------------------------------------------------------------------------------------*/
}