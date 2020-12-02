package com.example.sapper.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.example.sapper.constant.GameConstant
import com.example.sapper.R
import kotlinx.android.synthetic.main.activity_game_results.*


class GameResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_results)

        val result = intent.getBooleanExtra(GameConstant().GAME_RESULT, true)
        tv_game_result_result.text = if (result) {
            resources.getString(R.string.win)
        } else resources.getString(R.string.lose)

        tv_game_result_height.text =
            intent.getIntExtra(GameConstant().EXTRA_HEIGHT,0).toString()
        tv_game_result_width.text =
            intent.getIntExtra(GameConstant().EXTRA_WIDTH,0).toString()
        tv_game_result_mines_count.text =
            intent.getIntExtra(GameConstant().EXTRA_MINES_COUNT,0).toString()
        tv_game_result_minutes.text =
            intent.getIntExtra(GameConstant().EXTRA_GAME_TIME_MINUTES,0).toString()
        tv_game_result_seconds.text =
            intent.getIntExtra(GameConstant().EXTRA_GAME_TIME_SECONDS,0).toString()
    }

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
    }

    fun onConfirmButtonClick(view: View) {
        finish()
    }
}