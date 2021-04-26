package com.example.sapper.view.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.example.sapper.model.constant.GameConstant
import com.example.sapper.R
import com.example.sapper.model.constant.Constant
import com.example.sapper.model.entity.local.Game
import com.example.sapper.controller.logic.AsynсWorker
import com.example.sapper.dialog.DialogHelp
import com.example.sapper.view.Utils
import kotlinx.android.synthetic.main.activity_game_results.*


class GameResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.onActivityCreateSetTheme(this)
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
                    val completedLevel = intent.getIntExtra(GameConstant().EXTRA_GAME_ID,1)
                    AsynсWorker().setLevelCompleted(this,completedLevel)
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
}