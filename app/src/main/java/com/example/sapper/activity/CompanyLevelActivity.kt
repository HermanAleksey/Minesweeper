package com.example.sapper.activity

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.sapper.constant.Constant
import com.example.sapper.R
import com.example.sapper.activity.MinefieldActivity.activity.MinefieldActivity
import com.example.sapper.constant.GameConstant
import com.example.sapper.entity.CompanyGame
import com.example.sapper.entity.Field
import kotlinx.android.synthetic.main.activity_company_level.*

class CompanyLevelActivity : AppCompatActivity() {

    private lateinit var db: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_level)

        db = baseContext.openOrCreateDatabase("app.db", MODE_PRIVATE, null)
//        val numberOfLevels = DAOCompanyLevel(db).getTheNumberOfRecords()
        /**----------------------------GENERATING NUM OF LEVELS--------------------------**/
        val numberOfLevels = 46
        var numberOfLines = numberOfLevels / 4
        val numOfElemsOnLastLine = numberOfLevels % 4
        if (numOfElemsOnLastLine != 0) numberOfLines++

        val metrics = resources.displayMetrics
        var height = 100
        when (metrics.densityDpi) {
            480 -> height = 120
        }
        val layoutParams: ViewGroup.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            height
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

            if (i == numberOfLines - 1) {
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
            val companyLevel = CompanyGame(1, Field(4, 4, 2), 2, 0, false)

            val myIntent = Intent(this, MinefieldActivity::class.java)
            myIntent.putExtra(
                Constant().EXTRA_GAME_MODE,
                Constant().EXTRA_GAME_MODE_COMPANY
            )
            myIntent.putExtra(
                GameConstant().EXTRA_GAME_OBJECT,
                companyLevel
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