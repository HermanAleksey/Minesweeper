package com.example.sapper.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.sapper.constant.Constant
import com.example.sapper.R
import kotlinx.android.synthetic.main.activity_company_level.*

class CompanyLevelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_level)

        val layoutParams: ViewGroup.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            80
        )
        val buttonLayoutParams: ViewGroup.LayoutParams = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.MATCH_PARENT,
            1.0.toFloat()
        )

        val buttonsArray = ArrayList<Button>()

        /*generation of level buttons*/
        for (i in 0..30) {
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL

            linearLayout.id = linearLayout.hashCode()
            ll_scrlv_company_level_activity_levels.addView(linearLayout, layoutParams)

            for (j in 0..3) {
                val button = Button(this)
                button.text = "${i * 4 + 1 + j}"
                button.id = 0 + i * 10 + j
                linearLayout.addView(button, buttonLayoutParams)
                buttonsArray.add(button)
            }
        }

        /*onClickListener*/
        val onLevelButtonClickListener = View.OnClickListener {
            it as Button
            val intent = Intent(this, MinefieldActivity::class.java)
            val levelNumber = it.text
            intent.putExtra(Constant().GAME_MODE, Constant().GAME_MODE_COMPANY)
            intent.putExtra(Constant().COMPANY_LEVEL, levelNumber)
            startActivity(intent)
        }
        buttonsArray.forEach { it.setOnClickListener(onLevelButtonClickListener) }

        btn_company_level_activity_back.setOnClickListener { finish() }
    }
}