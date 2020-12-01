package com.example.sapper.activity.MinefieldActivity.activity

import HostField
import Saper
import UserField
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.media.SoundPool
import android.opengl.Visibility
import android.os.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sapper.R
import com.example.sapper.activity.DeviceListActivity
import com.example.sapper.activity.GameResultsActivity
import com.example.sapper.activity.MainActivity
import com.example.sapper.activity.MinefieldActivity.IMinefieldActivity
import com.example.sapper.constant.BluetoothConstant
import com.example.sapper.constant.Constant
import com.example.sapper.constant.GameConstant
import com.example.sapper.entity.CompanyLevel
import com.example.sapper.entity.Room
import com.example.sapper.logic.MinefieldAdapter
import com.example.sapper.logic.MultiPlayerService
import com.example.sapper.logic.SoundPoolWorker
import com.example.sapper.logic.TimeWorker
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_minefield.*
import kotlinx.android.synthetic.main.activity_test_chat.*
import kotlinx.android.synthetic.main.spinner_layout_game_settings.*

class MineFieldBTActivity : AppCompatActivity(), IMinefieldActivity {
    var hostField: HostField? = null

    var gameTimerMilli: Long = 0
    private var countDownTimer: CountDownTimer? = null

    lateinit var handler: Handler

    var millisecondTime: Long = 0
    var startTime: Long = 0
    var timeBuff: Long = 0
    var updateTime: Long = 0
    var seconds: Int = 0
    var minutes: Int = 0
    var milliSeconds: Int = 0
    private var runnable: Runnable = object : Runnable {
        override fun run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime
            updateTime = timeBuff + millisecondTime
            seconds = (updateTime / 1000).toInt()
            minutes = seconds / 60
            seconds %= 60
            milliSeconds = (updateTime % 1000).toInt()
            tv_minefield_seconds.text = "${
                if (seconds < 10) {
                    "0$seconds"
                } else seconds
            }"
            tv_minefield_minutes.text = "${
                if (minutes < 10) {
                    "0$minutes"
                } else minutes
            }"
            handler.postDelayed(this, 0)
        }
    }
    private lateinit var timeWorker: TimeWorker
    private lateinit var soundPoolWorker: SoundPoolWorker

    private lateinit var soundPool: SoundPool
    private var soundExplosion: Int = 0
    private var soundWin: Int = 0
    private var soundFlagDrop: Int = 0
    private var soundTap: Int = 0

    /** ---------------------------- Bluetooth implementation --------------------------- **/
    private var socketView = 0

    // Local Bluetooth adapter
    private var mBluetoothAdapter: BluetoothAdapter? = null

    // Member object for the chat services
    private var mChatService: MultiPlayerService? = null
    private val gson = Gson()
    private var role = ""
    private lateinit var roomInfo: Room
    private var GSONobject = ""

    private var tagString = "---------Start---------\n"

    /**-----------------------------------------------------------------------------------**/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minefield)
        roomInfo = Room(10, 10, 16, 3, 0, false)
        GSONobject = gson.toJson(roomInfo)

        /* --------------------Bluetooth--------------------- */
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mChatService == null) {
            setupChat()
        }

        /*sound pool settings for diff versions*/
        soundPoolWorker = SoundPoolWorker()
        soundPool = soundPoolWorker.getSoundPool()
        loadSounds()

        /*For timer*/
        handler = Handler()

        when (intent.getStringExtra(Constant().EXTRA_BLUETOOTH_ROLE)) {
            /*If Server:
            1) turn on discoverable
            2) process Intent with minefield setting
             */
            Constant().ROLE_SERVER -> {
                roomInfo = intent.getSerializableExtra(GameConstant().EXTRA_ROOM) as Room
                role = Constant().ROLE_SERVER
                // Ensure this device is discoverable by others
                ensureDiscoverable()

                /*filling view*/
                fillViewElements()
            }
            /*If Client:
            1) Launch the DeviceListActivity to see devices and do scan
             */
            Constant().ROLE_CLIENT -> {
                role = Constant().ROLE_CLIENT
                val serverIntent =
                    Intent(MainActivity.context, DeviceListActivity::class.java)
                startActivityForResult(
                    serverIntent,
                    BluetoothConstant.REQUEST_CONNECT_DEVICE
                )
            }
        }

//        startStopWatch()
    }

    private fun configureFields() {
        /*Visual minefield (from buttons)*/
        val arrayButtonsField =
            MinefieldAdapter().createMinefield(
                roomInfo.width, roomInfo.height, linear_layout_minefield, this
            )
        hostField = HostField(roomInfo.width, roomInfo.height, roomInfo.minesCount)
        val userField = UserField(roomInfo.width, roomInfo.height, roomInfo.minesCount)

        MinefieldAdapter().setupMinefield(userField.content, arrayButtonsField)

        setOnClickListenerForField(
            arrayButtonsField,
            userField.content
        )

        /*SeekBar for controlling cells size*/
        configureSeekBar(arrayButtonsField)
    }

    private fun startStopWatch() {
        /*stopwatch / timer starting*/
        timeWorker = TimeWorker(this)
        gameTimerMilli = timeWorker.translateToMilli("${roomInfo.minutes}:${roomInfo.seconds}")
        if (gameTimerMilli == 0L) {
            startTime = SystemClock.uptimeMillis()
            runnable.run()
        } else {
            timeWorker.getCountDownTimer(
                tv_minefield_minutes,
                tv_minefield_seconds, gameTimerMilli
            ).start()
        }
    }

    private fun configureSeekBar(arrayButtonsField: Array<Array<Button>>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seek_bar_minefield_cell_size.min = 30
        }
        seek_bar_minefield_cell_size.max = 200
        seek_bar_minefield_cell_size.setOnSeekBarChangeListener(object :
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
    }

    override fun fillViewElements() {
        tv_minefield_field_width.text = "${roomInfo.width}"
        tv_minefield_field_height.text = "${roomInfo.height}"
        tv_minefield_minutes.text = if (roomInfo.minutes < 10) {
            "0${roomInfo.minutes}"
        } else "${roomInfo.minutes}"
        tv_minefield_seconds.text = if (roomInfo.seconds < 10) {
            "0${roomInfo.seconds}"
        } else "${roomInfo.seconds}"
        tv_minefield_mines.text = "${roomInfo.minesCount}"
    }

    override fun loadSounds() {
        soundExplosion = soundPool.load(this, R.raw.explosion_8bit, 1)
        soundWin = soundPool.load(this, R.raw.win_01, 1)
        soundFlagDrop = soundPool.load(this, R.raw.flag_drop, 1)
        soundTap = soundPool.load(this, R.raw.new_tap, 1)
    }

    /*define how each cell gonna react to click with flag/open selected*/
    override fun setOnClickListenerForField(
        arrayButtonsField: Array<Array<Button>>,
        userField: Array<Array<Char>>
    ) {
        for (y in arrayButtonsField.indices) {
            for (x in arrayButtonsField[y].indices) {
                arrayButtonsField[x][y].setOnClickListener {
                    /*Opener*/
                    if (!togglebutton_minefield_flag.isChecked) {
//                        if (hostField == null) {
//                            hostField = HostField(width, height, minesCount, x, y)
//                        }
                        soundPoolWorker.playSound(soundPool, soundTap)

                        val keepGame = Saper().openCoordinate(x, y, hostField!!.content, userField)
                        if (!keepGame) {
                            notifyLose()
                        } else {
                            /*если не проиграл - проверить, возможно теперь условия выполняются.*/
                            /*т.к. openCoordinate возвращает false только если проиграл и не отличает
                            * продолжение игры от победы*/
                            if (Saper().checkWinCondition(hostField!!.content, userField)) {
                                notifyWin()
                            }
                        }
                        MinefieldAdapter().setupMinefield(userField, arrayButtonsField)
                    }
                    /*FLAG*/
                    if (togglebutton_minefield_flag.isChecked) {
//                        if (hostField == null) {
//                            hostField = HostField(width, height, minesCount, x, y)
//                        }
                        soundPoolWorker.playSound(soundPool, soundFlagDrop)
                        val win = Saper().useFlagOnSpot(x, y, hostField!!.content, userField)
                        MinefieldAdapter().setupMinefield(userField, arrayButtonsField)
                        if (win) {
                            notifyWin()
                        }
                    }
                }
            }
        }
    }

    override fun performEndEvents(result: Boolean) {
        val sound = if (result) soundWin else soundExplosion
        soundPoolWorker.playSound(soundPool, sound)
        tv_minefield_seconds.postDelayed({
            intentToResultActivity(result)
            if (countDownTimer != null) {
                countDownTimer!!.cancel()
            }
        }, 500)
    }

    override fun intentToResultActivity(result: Boolean) {
        val mIntent = Intent(this, GameResultsActivity::class.java)

        mIntent.putExtra(
            GameConstant().GAME_RESULT,
            result
        )
        mIntent.putExtra(
            GameConstant().WIDTH_TAG,
            tv_minefield_field_width.text.toString().toInt()
        )
        mIntent.putExtra(
            GameConstant().HEIGHT_TAG,
            tv_minefield_field_height.text.toString().toInt()
        )
        mIntent.putExtra(
            GameConstant().MINES_COUNT_TAG,
            tv_minefield_mines.text.toString().toInt()
        )
        if (gameTimerMilli == 0L) {
            mIntent.putExtra(
                GameConstant().GAME_TIME_MINUTES_TAG,
                tv_minefield_minutes.text.toString().toInt()
            )
            mIntent.putExtra(
                GameConstant().GAME_TIME_SECONDS_TAG,
                tv_minefield_seconds.text.toString().toInt()
            )
        } else {
            var seconds = roomInfo.seconds - tv_minefield_seconds.text.toString().toInt()
            var minutes = roomInfo.seconds - tv_minefield_minutes.text.toString().toInt()
            if (seconds < 0) {
                seconds += 60
                minutes--
            }
            mIntent.putExtra(
                GameConstant().GAME_TIME_SECONDS_TAG,
                seconds
            )
            mIntent.putExtra(
                GameConstant().GAME_TIME_MINUTES_TAG,
                minutes
            )
        }
        startActivity(mIntent)
        finish()
    }

    /**-------------------------------------------------------Bluetooth methods--------------------------------------------------------*/

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private fun sendSocketMessages(message: String) {
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

    private fun setupChat() {
        Log.d(BluetoothConstant.TAG, "setupChat()")
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = MultiPlayerService(this, mHandler)
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

    fun textViewAppend(str: String) {
        tagString += "\n-----------------------------\n$str"

    }

    // The Handler that gets information back from the BluetoothChatService
    private val mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                BluetoothConstant.BOARD_POST -> {
                }
                BluetoothConstant.MESSAGE_SAPPER_LOSS -> sendSocketMessages("Miner won")
                BluetoothConstant.MESSAGE_SAPPER_WIN -> sendSocketMessages("Sapper won")
                BluetoothConstant.MESSAGE_STATE_CHANGE -> {
                    Log.i(
                        BluetoothConstant.TAG,
                        "MESSAGE_STATE_CHANGE: " + msg.arg1
                    )
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
                    tagString += "\n----------new----------\n$readMessage"
                    when {
                        /**-----------------------------------Processing JSON with ROOM configs-----------------------------------------------*/
                        readMessage.startsWith('{') -> {
                            roomInfo = gson.fromJson(readMessage, Room::class.java)
                            textViewAppend(roomInfo.toString() + "after startsWith('{')")
//                            configureFields()
//
//                            /*filling view*/
//                            fillViewElements()
                            /**-----------------Request to start game -------------------*/
                            sendSocketMessages("StartRequest")
                            textViewAppend("Sending @StartRequest@ after startsWith('{')")
                        }
                        /**---------------Start request was accepted ON ----------------*/
                        readMessage == "StartRequest" -> {
                            sendSocketMessages("AcceptStartRequest")
                            textViewAppend("Sending @AcceptStartRequest@ after StartRequest")
                        }
                        readMessage == "AcceptStartRequest" -> {
                            textViewAppend("Vgot after AcceptStartRequest")
                        }
                        /**-----------Если пришло сообщение "Lose" ------------------*/
                        //тогда настоящий пользователь автоматически победил
                        readMessage == "Lose" -> {
                            Toast.makeText(
                                this@MineFieldBTActivity,
                                "Gratz, u won",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        /**-----------Если пришло сообщение "Win" ------------------*/
                        //тогда настоящий пользователь автоматически победил
                        readMessage == "Win" -> {
                            Toast.makeText(
                                this@MineFieldBTActivity,
                                "sorry, u lost",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }
                BluetoothConstant.MESSAGE_DEVICE_NAME -> {
                    Toast.makeText(
                        applicationContext,
                        "R.string.connectTo.toString()" + msg.data.getString(BluetoothConstant.DEVICE_NAME),
                        Toast.LENGTH_SHORT
                    ).show()
                    /**--------------First message after connection --------------**/
                    when (role) {
                        "Client" -> {
                        }
                        "Server" -> {
                            Log.e("TAG", "Server sending room info ")
                            //Sending room info as GSON
                            textViewAppend(gson.toJson(roomInfo)+" sended after @Server@ accepted client")
                            sendSocketMessages(gson.toJson(roomInfo))
                            // Generating room depends on sameField factor
                            configureFields()
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

    /*private val mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                BluetoothConstant.BOARD_POST -> {
                }
                BluetoothConstant.MESSAGE_SAPPER_LOSS -> sendSocketMessages("Miner won")
                BluetoothConstant.MESSAGE_SAPPER_WIN -> sendSocketMessages("Sapper won")
                BluetoothConstant.MESSAGE_STATE_CHANGE -> {
                    Log.i(
                        BluetoothConstant.TAG,
                        "MESSAGE_STATE_CHANGE: " + msg.arg1
                    )
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
                    tagString += "\n----------new----------\n$readMessage"
                    textViewChat.text = tagString
                    when {
                        /**-----------------------------------Processing JSON with ROOM configs-----------------------------------------------*/
                        readMessage.startsWith('{') -> {
                            val room = gson.fromJson(readMessage, CompanyLevel::class.java)
                            textViewAppend("Client get and processing room info gson:")
                            textViewAppend(room.toString())

                            /**-----------------Request to start game -------------------*/
                            sendSocketMessages("StartRequest")
                            textViewAppend("Client sande startRequest")
                        }
                        /**------------------------Start request was accepted ON -------------------------*/
                        readMessage == "StartRequest" -> {
                            textViewAppend("Server got start request")
                            sendSocketMessages("AcceptStartRequest")
                            textViewAppend("Server send accept request")
                        }
                        readMessage == "AcceptStartRequest" -> {
                            textViewAppend("Client got answer to start request")
                        }
                        /**-----------Если пришло сообщение "Lose" ------------------*/
                        //тогда настоящий пользователь автоматически победил
                        readMessage == "Lose" -> {
                            Toast.makeText(
                                this@MineFieldBTActivity,
                                "Gratz, u won",
                                Toast.LENGTH_SHORT
                            ).show()
                            textViewAppend("\n\n\nI WON!")
                        }
                        /**-----------Если пришло сообщение "Win" ------------------*/
                        //тогда настоящий пользователь автоматически победил
                        readMessage == "Win" -> {
                            Toast.makeText(
                                this@MineFieldBTActivity,
                                "sorry, u lost",
                                Toast.LENGTH_SHORT
                            ).show()
                            textViewAppend("\n\n\nI LOSE!")
                        }

                    }
                }
                BluetoothConstant.MESSAGE_DEVICE_NAME -> {
                    Toast.makeText(
                        applicationContext,
                        "R.string.connectTo.toString()" + msg.data.getString(BluetoothConstant.DEVICE_NAME),
                        Toast.LENGTH_SHORT
                    ).show()
                    /**--------------------------------------First message after connection -----------------------------------------------**/
                    when (role) {
                        "Client" -> {
                        }
                        "Server" -> {
                            textViewAppend("Server sending room info")
                            textViewAppend("gson: ")
                            //Sending room info as GSON
                            sendSocketMessages(GSONobject)
                        }
                    }
                }
                BluetoothConstant.MESSAGE_TOAST -> {
                    Toast.makeText(
                        applicationContext, msg.data.getString(BluetoothConstant.TOAST), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }*/

    private fun notifyWin() {
        sendSocketMessages("Win")
    }

    private fun notifyLose() {
        sendSocketMessages("Lose")
    }

    /**-------------------------------------------------------overriding standard methods--------------------------------------------------------*/
    @Synchronized
    override fun onResume() {
        super.onResume()
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

//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        tv_minefield_field_width.text =
//            savedInstanceState.getInt(GameConstant().WIDTH_TAG).toString()
//        tv_minefield_field_height.text =
//            savedInstanceState.getInt(GameConstant().HEIGHT_TAG).toString()
//        tv_minefield_mines.text =
//            savedInstanceState.getInt(GameConstant().MINES_COUNT_TAG).toString()
//
//        //BT implementation
//
//    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putInt(
//            GameConstant().WIDTH_TAG,
//            tv_minefield_field_width.text.toString().toInt()
//        )
//        outState.putInt(
//            GameConstant().HEIGHT_TAG,
//            tv_minefield_field_height.text.toString().toInt()
//        )
//        outState.putInt(
//            GameConstant().MINES_COUNT_TAG,
//            tv_minefield_mines.text.toString().toInt()
//        )
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_toolbar_rules) {
            showGameRulesAlertDialog(this)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showGameRulesAlertDialog(context: Context) {
        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.rules)
            .setMessage("R.string.rulesOfTheGame")
            .setPositiveButton(R.string.understand, null)
            .show()

        val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setTextColor(context.resources.getColor(R.color.colorPrimaryDark))
    }

    override fun onBackPressed() {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        soundPool.release()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        soundPool.release()

        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService!!.stop()
    }

}