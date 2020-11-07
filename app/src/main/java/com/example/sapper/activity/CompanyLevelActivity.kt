package com.example.sapper.activity

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.sapper.constant.Constant
import com.example.sapper.R
import com.example.sapper.constant.GameConstant
import com.example.sapper.db.DAOCompanyLevel
import com.example.sapper.entity.CompanyLevel
import kotlinx.android.synthetic.main.activity_company_level.*
import kotlinx.android.synthetic.main.new_activity_game_settings.*

class CompanyLevelActivity : AppCompatActivity() {

    private lateinit var db: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_level)

        db = baseContext.openOrCreateDatabase("app.db", MODE_PRIVATE, null)
//        val numberOfLevels = DAOCompanyLevel(db).getTheNumberOfRecords()
        val numberOfLevels = 46
        var numberOfLines = numberOfLevels / 4
        val numOfElemsOnLastLine = numberOfLevels % 4
        if (numOfElemsOnLastLine != 0) numberOfLines++

        val layoutParams: ViewGroup.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            100
        )
        val buttonLayoutParams: ViewGroup.LayoutParams = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.MATCH_PARENT,
            1.0.toFloat()
        )

        val buttonsArray = ArrayList<Button>()

        /*generation of level buttons*/
        for (i in 0 until numberOfLines) {
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL

            linearLayout.id = linearLayout.hashCode()
            ll_scrlv_company_level_activity_levels.addView(linearLayout, layoutParams)

            if (i == numberOfLines-1) {
                for (j in 0 until numOfElemsOnLastLine) {
                    val button = Button(this)
                    button.text = "${i * 4 + 1 + j}"
                    button.id = 0 + i * 10 + j
                    linearLayout.addView(button, buttonLayoutParams)
                    buttonsArray.add(button)
                }
            } else {
                for (j in 0..3) {
                    val button = Button(this)
                    button.text = "${i * 4 + 1 + j}"
                    button.id = 0 + i * 10 + j
                    linearLayout.addView(button, buttonLayoutParams)
                    buttonsArray.add(button)
                }
            }
        }

        /*onClickListener*/
        val onLevelButtonClickListener = View.OnClickListener {
            it as Button
            /*by level number picking all values from DB*/
            val levelNumber = it.text
            val companyLevel = CompanyLevel(1, 4, 4, 1, 1, 1, true)


            val myIntent = Intent(this, MinefieldActivity::class.java)
            myIntent.putExtra(Constant().GAME_MODE, Constant().GAME_MODE_COMPANY)
//            myIntent.putExtra(Constant().COMPANY_LEVEL, levelNumber)
            myIntent.putExtra(
                GameConstant().HEIGHT_TAG,
                companyLevel.height
            )
            myIntent.putExtra(
                GameConstant().WIDTH_TAG,
                companyLevel.width
            )
            myIntent.putExtra(
                GameConstant().MINES_COUNT_TAG,
                companyLevel.minesCount
            )
            myIntent.putExtra(
                GameConstant().GAME_TIME_MINUTES_TAG,
                companyLevel.minutes
            )
            myIntent.putExtra(
                GameConstant().GAME_TIME_SECONDS_TAG,
                companyLevel.seconds
            )
            myIntent.putExtra(
                GameConstant().FIRST_CLICK_MINE_TAG,
                false
            )
            startActivity(myIntent)
        }
        buttonsArray.forEach { it.setOnClickListener(onLevelButtonClickListener) }

        btn_company_level_activity_back.setOnClickListener { finish() }
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }
}