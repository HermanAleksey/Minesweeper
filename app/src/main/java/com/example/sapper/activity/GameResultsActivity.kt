package com.example.sapper.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.example.sapper.GameConstant
import com.example.sapper.R
import kotlinx.android.synthetic.main.activity_game_results.*
import kotlinx.android.synthetic.main.activity_minefield.*


class GameResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_results)

        val result = intent.getBooleanExtra(GameConstant().GAME_RESULT, false)
        tv_game_result_result.text = if (result) {
            resources.getString(R.string.win)
        } else resources.getString(R.string.lose)

        tv_game_result_height.text =
            intent.getIntExtra(GameConstant().HEIGHT_TAG,0).toString()
        tv_game_result_width.text =
            intent.getIntExtra(GameConstant().WIDTH_TAG,0).toString()
        tv_game_result_mines_count.text =
            intent.getIntExtra(GameConstant().MINES_COUNT_TAG,0).toString()
        tv_game_result_minutes.text =
            intent.getIntExtra(GameConstant().GAME_TIME_MINUTES_TAG,0).toString()
        tv_game_result_seconds.text =
            intent.getIntExtra(GameConstant().GAME_TIME_SECONDS_TAG,0).toString()
    }

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
    }

    fun onConfirmButtonClick(view: View) {}
}