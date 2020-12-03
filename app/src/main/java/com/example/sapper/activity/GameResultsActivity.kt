package com.example.sapper.activity

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.room.Room
import com.example.sapper.constant.GameConstant
import com.example.sapper.R
import com.example.sapper.constant.Constant
import com.example.sapper.db.AppDatabase
import com.example.sapper.entity.Game
import kotlinx.android.synthetic.main.activity_game_results.*


class GameResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_results)

        val result = intent.getBooleanExtra(GameConstant().GAME_RESULT, true)
        tv_game_result_result.text = if (result) {
            resources.getString(R.string.win)
        } else resources.getString(R.string.lose)

        val gameMode = when (intent.getStringExtra(Constant().EXTRA_GAME_MODE)) {
            Constant().EXTRA_GAME_MODE_CREATIVE -> resources.getString(R.string.gameModeCasual)
            Constant().EXTRA_GAME_MODE_COMPANY -> {
                //if level was passed (completed) - update DB
                if (result) {
                    object: Thread() {
                        override fun run() {
                            super.run()
                            val db = Room.databaseBuilder(
                                applicationContext,
                                AppDatabase::class.java, "database-name"
                            ).build()
                            val dao = db.getCompanyGameDao()
                            val completedLevel = intent.getIntExtra(GameConstant().EXTRA_GAME_ID,1)
                            dao.setCompleted(completedLevel)
                        }
                    }.start()
                }
                resources.getString(R.string.gameModeCompany)
            }
            Constant().EXTRA_GAME_MODE_BLUETOOTH -> resources.getString(R.string.gameModeBluetooth)
            else -> "None"
        }
        val game = intent.getSerializableExtra(GameConstant().EXTRA_GAME_OBJECT) as Game

        tv_game_result_game_mode.text = gameMode
        tv_game_result_height.text = game.field.height.toString()
        tv_game_result_width.text = game.field.width.toString()
        tv_game_result_mines_count.text = game.field.minesCount.toString()
        tv_game_result_minutes.text = game.minutes.toString()
        tv_game_result_seconds.text = game.seconds.toString()
    }

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
    }

    fun onConfirmButtonClick(view: View) {
        finish()
    }
}