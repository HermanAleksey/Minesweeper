package com.example.sapper.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.sapper.model.constant.Constant
import com.example.sapper.R
import com.example.sapper.view.activity.MinefieldActivity.activity.MinefieldActivity
import com.example.sapper.model.constant.GameConstant
import com.example.sapper.controller.db.AppDatabase
import com.example.sapper.model.entity.local.CompanyGame
import com.example.sapper.dialog.DialogHelp
import com.example.sapper.view.Utils
import kotlinx.android.synthetic.main.activity_company_level.*

class CompanyLevelActivity : AppCompatActivity() {

    private val buttonsArray = mutableListOf<Button>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*theme processing*/
        Utils.onActivityCreateSetTheme(this)

        setContentView(R.layout.activity_company_level)

        /**----------------------------GENERATING NUM OF LEVELS--------------------------**/

        //TODO("" 1 жизнь за рекламу)

        object : Thread() {
            override fun run() {
                super.run()
                val db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java, "database-name"
                ).build()
                val dao = db.getCompanyGameDao()
                val numberOfLevels = dao.getCount()
                Log.e("TAG", "run: numberOfLevels:$numberOfLevels")
                runOnUiThread { generateLevelButtons(numberOfLevels) }
            }
        }.start()
    }

    private fun generateLevelButtons(num: Int) {
        var numberOfLines = num / 4
        val numOfElemsOnLastLine = num % 4
        if (numOfElemsOnLastLine != 0) numberOfLines++

        val metrics = resources.displayMetrics
        var height = 150
        when (metrics.densityDpi) {
            480 -> height = 180
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

        /*generation of level buttons*/
        for (i in 0 until numberOfLines) {
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL

            linearLayout.id = linearLayout.hashCode()
            ll_scrlv_company_level_activity_levels.addView(linearLayout, layoutParams)

            if (i == numberOfLines - 1) {
                for (j in 0 until numOfElemsOnLastLine) {
                    val button = Button(this,null, R.attr.company_level_buttons)
                    button.text = "${i * 4 + 1 + j}"
                    button.id = 0 + i * 10 + j
//                    button.setTextColor(resources.getColor(R.color.colorBlack))
                    linearLayout.addView(button, buttonLayoutParams)
                    buttonsArray.add(button)
                }
            } else {
                for (j in 0..3) {
                    val button = Button(this,null ,R.attr.company_level_buttons)
                    button.text = "${i * 4 + 1 + j}"
                    button.id = 0 + i * 10 + j
//                    button.setTextColor(resources.getColor(R.color.colorBlack))
                    linearLayout.addView(button, buttonLayoutParams)
                    buttonsArray.add(button)
                }
            }
            Log.e("TAG", "generateLevelButtons: ${buttonsArray.size}")
        }

        setupOnClickListeners()
    }

    private fun setupOnClickListeners() {
        buttonsArray.forEach {
            it.setOnClickListener(onLevelButtonClickListener)
            Log.e("", "onCreate: calling setOnClickListener")
        }
        btn_company_level_activity_back.setOnClickListener { finish() }
    }

    private val onLevelButtonClickListener = View.OnClickListener {
        it as Button
        /*by level number picking all values from DB*/
        val levelNumber = it.text.toString().toInt()
        object : Thread() {
            override fun run() {
                super.run()
                val db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java, "database-name"
                ).build()
                val dao = db.getCompanyGameDao()
                val game = dao.get(levelNumber)

                Log.e("TAG", "run: numberOfLevels:${game.unpack()}")
                runOnUiThread { startGame(game.unpack()) }
            }
        }.start()
    }

    private fun startGame(companyLevel: CompanyGame) {
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