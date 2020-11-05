package com.example.sapper.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.example.sapper.GameConstant
import com.example.sapper.R
import kotlinx.android.synthetic.main.activity_game_results.*


class GameResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_results)

        val result = intent.getBooleanExtra(GameConstant().GAME_RESULT, false)
        tv_game_result_result.text = if (result) {
            resources.getString(R.string.win)
        } else resources.getString(R.string.lose)
    }

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
    }
}