package com.example.sapper.view.activity.ChatRoomActivity

import android.content.Intent
import android.media.SoundPool
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.lifecycle.Observer
import com.bsuir.herman.authscreenapp.IMessageCallback
import com.bsuir.herman.saper.HostField
import com.bsuir.herman.saper.Saper
import com.bsuir.herman.saper.UserField
import com.example.sapper.R
import com.example.sapper.controller.logic.MinefieldAdapter
import com.example.sapper.controller.logic.SoundPoolWorker
import com.example.sapper.controller.logic.TimeWorker
import com.example.sapper.databinding.ActivityChatRoomBinding
import com.example.sapper.dialog.DialogHelp
import com.example.sapper.model.constant.Constant
import com.example.sapper.model.constant.GameConstant
import com.example.sapper.model.dto.RoomDTO
import com.example.sapper.model.entity.local.Field
import com.example.sapper.model.entity.local.Game
import com.example.sapper.controller.network.WebSocketHandler
import com.example.sapper.view.Utils
import com.example.sapper.view.activity.GameResultsActivity
import com.example.sapper.view.activity.MinefieldActivity.IMinefieldActivity

class ChatRoomActivity : AppCompatActivity(), IMessageCallback, IMinefieldActivity {

    private lateinit var roomSettings: RoomDTO
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

    lateinit var stopWatchRunnable: Runnable

    /**---------------------------WebSocket var---------------------------*/
    private lateinit var binding: ActivityChatRoomBinding
    private var ImPlayerNumber: Int = 0

    private fun startStopWatch() {
        /*stopwatch / timer starting*/
        gameTimerMilli =
            timeWorker.translateToMilli("${this.roomSettings.timeMin}:${this.roomSettings.timeSec}")
        if (gameTimerMilli == 0L) {
            val startTime = SystemClock.uptimeMillis()
            stopWatchRunnable = timeWorker.getStopWatchRunnable(startTime)
            stopWatchRunnable.run()
        } else {
            countDownTimer = timeWorker.getCountDownTimer(
                binding.tvChatRoomMinutes,
                binding.tvChatRoomSeconds,
                gameTimerMilli
            ).start()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_toolbar_rules) {
            showGameRulesAlertDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showGameRulesAlertDialog() {
        val dialog = DialogHelp()
        dialog.show(supportFragmentManager, Constant().HELPER_DIALOG)
    }

    private lateinit var currentUsername: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        val view: View = binding.root
        Utils.onActivityCreateSetTheme(this)
        setContentView(view)

        ViewModel.uname1().observe(this, observer1)
        ViewModel.uname2().observe(this, observer2)
        WebSocketHandler.setMessageCallbackListener(this)
        currentUsername = getSharedPreferences(
            Constant().APP_PREFERENCE_USER,
            MODE_PRIVATE
        ).getString(Constant().CURRENT_USER_NAME, "")!!

        //timer
        handler = Handler()
        timeWorker = TimeWorker(this, handler)

        //configure sound pool
        soundPoolWorker = SoundPoolWorker()
        soundPool = soundPoolWorker.getSoundPool()
        loadSounds()

        //processing intent bundle. Filling fields
        roomSettings = intent.getSerializableExtra(GameConstant().EXTRA_GAME_OBJECT) as RoomDTO

        //waiting room
        configureWaitingRoomViews()

        //click listeners
        //waiting room
        binding.btnChatWaitingRoomStartGame.setOnClickListener {
            if (binding.tvChatWaitingRoomPlayer1Name.text != "null"
                && binding.tvChatWaitingRoomPlayer2Name.text != "null"
            ) {
                if (binding.chbWaitingRoomPlayer1Ready.isChecked && binding.chbWaitingRoomPlayer2Ready.isChecked) {
                    sendStartGameRequest()
                } else {
                    when (currentUsername) {
                        binding.tvChatWaitingRoomPlayer1Name.text.toString() -> ImPlayerNumber = 1
                        binding.tvChatWaitingRoomPlayer2Name.text.toString() -> ImPlayerNumber = 2
                    }
                    setWssReadyRequest()
                }
            }
        }
        //chat room
        binding.btnChatRoomSend.setOnClickListener {
            Log.e("TAG", "sending message (button clicked)")
            sendMessage(binding.etChatRoomMessage.text.toString())
        }
        val debugClickList = View.OnClickListener {
            if (binding.llChatRoomGame.visibility == View.GONE) {
                binding.llChatRoomGame.visibility = View.VISIBLE
                binding.llChatRoomChat.visibility = View.GONE
            } else {
                binding.llChatRoomGame.visibility = View.GONE
                binding.llChatRoomChat.visibility = View.VISIBLE
            }
        }
        binding.btnDebug.setOnClickListener(debugClickList)

        //minefield room
        binding.tvDebug.setOnClickListener(debugClickList)
    }

    private fun configureWaitingRoomViews() {
        binding.tvChatWaitingRoomFieldHeight.text = this.roomSettings.height.toString()
        binding.tvChatWaitingRoomFieldWidth.text = this.roomSettings.width.toString()
        binding.tvChatWaitingRoomGameMode.text = resources.getString(R.string.internet)
        binding.tvChatWaitingRoomMinesAmount.text = this.roomSettings.minesCount.toString()
        binding.tvChatWaitingRoomTimeMin.text = this.roomSettings.timeMin.toString()
        binding.tvChatWaitingRoomTimeSec.text = this.roomSettings.timeSec.toString()
    }

    private fun configureSeekBar(arrayButtonsField: Array<Array<Button>>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.seekBarChatRoomCellSize.min = 30
        }
        binding.seekBarChatRoomCellSize.max = 200
        binding.seekBarChatRoomCellSize.setOnSeekBarChangeListener(object :
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
        binding.seekBarChatRoomCellSize.progress = 80
    }

    private fun setOnClickListenerForField(
        arrayButtonsField: Array<Array<Button>>,
        userField: Array<Array<Char>>
    ) {
        for (y in arrayButtonsField.indices) {
            for (x in arrayButtonsField[y].indices) {
                arrayButtonsField[x][y].setOnClickListener {
                    /*Opener*/
                    if (!binding.tgbtnChatRoomFlag.isChecked) {
                        soundPoolWorker.playSound(soundPool, soundTap)
                        val keepGame = Saper()
                            .openCoordinate(x, y, hostField.content, userField)
                        if (!keepGame) {
                            sendResultRequest(false)
                        } else {
                            /*если не проиграл - проверить, возможно теперь условия выполняются.*/
                            /*т.к. openCoordinate возвращает false только если проиграл и не отличает
                            * продолжение игры от победы*/
                            if (Saper()
                                    .checkWinCondition(hostField.content, userField)
                            ) {
                                sendResultRequest(true)
                            }
                        }
                        MinefieldAdapter().setupMinefield(userField, arrayButtonsField)
                    }
                    /*FLAGer*/
                    if (binding.tgbtnChatRoomFlag.isChecked) {
                        soundPoolWorker.playSound(soundPool, soundFlagDrop)
                        val win = Saper()
                            .useFlagOnSpot(x, y, hostField.content, userField)
                        MinefieldAdapter().setupMinefield(userField, arrayButtonsField)
                        if (win) {
                            sendResultRequest(true)
                        }
                    }
                }
            }
        }
    }

    private fun prepareFields() {
        hostField = HostField(
            this.roomSettings.width,
            this.roomSettings.height,
            this.roomSettings.minesCount
        )
        arrayButtonsField =
            MinefieldAdapter().createMinefield(
                this.roomSettings.width, this.roomSettings.height,
                binding.llChatRoomMinefield, this
            )
        configureSeekBar(arrayButtonsField)
        userField = UserField(
            this.roomSettings.width,
            this.roomSettings.height,
            this.roomSettings.minesCount
        )
        setOnClickListenerForField(
            arrayButtonsField,
            userField.content
        )
        MinefieldAdapter().setupMinefield(userField.content, arrayButtonsField)

        fillViewsWithValues(
            this.roomSettings.width,
            this.roomSettings.height,
            this.roomSettings.minesCount,
            this.roomSettings.timeMin,
            this.roomSettings.timeSec
        )

        startStopWatch()
    }

    private fun fillViewsWithValues(
        width: Int, height: Int,
        minesCount: Int, min: Int,
        sec: Int
    ) {
        binding.tvChatRoomFieldWidth.text = width.toString()
        binding.tvChatRoomFieldHeight.text = height.toString()
        binding.tvChatRoomMinutes.text = if (min < 10) {
            "0$min"
        } else "$min"
        binding.tvChatRoomSeconds.text = if (sec < 10) {
            "0$sec"
        } else "$sec"
        binding.tvChatRoomMines.text = minesCount.toString()
    }

    private fun performEndEvents(result: Boolean, arrayButtonsField: Array<Array<Button>>) {
        val sound = if (result) soundWin else soundExplosion
        soundPoolWorker.playSound(soundPool, sound)
        MinefieldAdapter().setMinefieldUnclickable(arrayButtonsField)
        binding.llChatRoomMinefield.postDelayed({
            intentToResultActivity(result)
            timeWorker.stopCountDownTimer(countDownTimer)
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
            Constant().EXTRA_GAME_MODE,
            Constant().EXTRA_GAME_MODE_INTERNET
        )
        mIntent.putExtra(
            GameConstant().GAME_RESULT,
            result
        )
        val width = binding.tvChatRoomFieldWidth.text.toString().toInt()
        val height = binding.tvChatRoomFieldHeight.text.toString().toInt()
        val minesCount = binding.tvChatRoomMines.text.toString().toInt()
        var timeMin: Int
        var timeSec: Int

        if (gameTimerMilli == 0L) {
            timeMin = binding.tvChatRoomMinutes.text.toString().toInt()
            timeSec = binding.tvChatRoomSeconds.text.toString().toInt()
        } else {
            timeMin = this.roomSettings.timeMin - binding.tvChatRoomMinutes.text.toString().toInt()
            timeSec = this.roomSettings.timeSec - binding.tvChatRoomSeconds.text.toString().toInt()
            if (timeSec < 0) {
                timeSec += 60
                timeMin -= 1
            }
        }

        val game = Game(Field(width, height, minesCount), timeMin, timeSec)

        mIntent.putExtra(GameConstant().EXTRA_GAME_OBJECT, game)

        startActivity(mIntent)
        finish()
    }

    override fun textViewMinutesSetText(str: String) {
        binding.tvChatRoomMinutes.text = str
    }

    override fun textViewSecondsSetText(str: String) {
        binding.tvChatRoomSeconds.text = str
    }

    /**------------------------------------------------------------------------WEB------------------------------------------------------------------------*/

    private val observer1 = Observer<String> {
        binding.tvChatRoomPlayer1.text = it
        binding.tvChatWaitingRoomPlayer1Name.text = it
    }
    private val observer2 = Observer<String> {
        binding.tvChatRoomPlayer2.text = it
        binding.tvChatWaitingRoomPlayer2Name.text = it
    }

    private fun sendMessage(str: String) {
        WebSocketHandler.writeMessage(this.roomSettings.id, str)
    }

    private fun setWssReadyRequest() {
        sendMessage(MARK_READY + "_$ImPlayerNumber")
    }

    private fun sendResultRequest(iWon: Boolean) {
        sendMessage(GAME_ENDED+"_${if (iWon)ImPlayerNumber else 3-ImPlayerNumber}")
    }

    private fun sendStartGameRequest() {
        sendMessage(START_GAME)
    }

    companion object {
        const val MARK_READY: String = "MARK_READY"
        const val MARK_NOT_READY: String = "MARK_NOT_READY"
        const val START_GAME: String = "START_GAME"

        const val GAME_ENDED_DRAW: String = "OPPONENT_WON"
        const val GAME_ENDED: String = "OPPONENT_WON"
    }

    override fun onMessageReceived(from: String, context: String) {
        Log.e("TAG", "onMessageReceived: callback on activity received.\ncontext:$context")

        when (context) {
            GAME_ENDED + "_0" -> {
                performEndEvents(false, arrayButtonsField)
                return
            }
            GAME_ENDED + "_1" -> {
                performEndEvents(ImPlayerNumber == 1, arrayButtonsField)
                return
            }
            GAME_ENDED + "_2" -> {
                performEndEvents(ImPlayerNumber == 2, arrayButtonsField)
                return
            }
            START_GAME -> {
                runOnUiThread {
                    binding.llChatWaitingRoom.visibility = View.GONE
                    prepareFields()
                }
            }
            MARK_READY + "_1" -> {
                runOnUiThread {
                    binding.chbWaitingRoomPlayer1Ready.isChecked = true
                }
            }
            MARK_READY + "_2" -> {
                runOnUiThread {
                    binding.chbWaitingRoomPlayer2Ready.isChecked = true
                }
            }
            else -> {
                concatMessage("From:$from\n$context\n")
                return
            }
        }
    }

    private fun makeToast(string: String) {
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
        // Stop the Bluetooth chat services
        WebSocketHandler.leaveRoomRequest(this.roomSettings.id)
        timeWorker.stopCountDownTimer(countDownTimer)
        soundPool.release()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        timeWorker.stopCountDownTimer(countDownTimer)
        soundPool.release()
        finish()
    }
}