package com.example.sapper.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.sapper.R
import com.example.sapper.dialog.DialogSettingMinesCount
import com.example.sapper.dialog.DialogSettingsSize
import kotlinx.android.synthetic.main.activity_game_settings.*

class CustomGameSettingsActivity : AppCompatActivity(),
    DialogSettingsSize.DialogSettingsSizeListener,
    DialogSettingMinesCount.DialogSettingMinesCountListener {

    val SETTING_SIZE_DIALOG: String = "com.example.sapper.DialogSettingSize"
    val SETTING_MINES_COUNT_DIALOG: String = "com.example.sapper.DialogSettingMinesCount"
    val HEIGHT_TAG: String = "com.example.sapper.height"
    val WIDTH_TAG: String = "com.example.sapper.width"
    val MINES_COUNT_TAG: String = "com.example.sapper.minesCount"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        button_game_settings_size_first.setOnClickListener(onToggleButtonClickListener)
        button_game_settings_size_second.setOnClickListener(onToggleButtonClickListener)
        button_game_settings_size_third.setOnClickListener(onToggleButtonClickListener)
        button_game_settings_size_fourth.setOnClickListener(onToggleButtonClickListener)
        button_game_settings_size_fifth.setOnClickListener(onToggleButtonClickListener)

        button_game_settings_mines_first.setOnClickListener(onToggleButtonClickListener)
        button_game_settings_mines_second.setOnClickListener(onToggleButtonClickListener)
        button_game_settings_mines_third.setOnClickListener(onToggleButtonClickListener)
        button_game_settings_mines_fourth.setOnClickListener(onToggleButtonClickListener)
        button_game_settings_mines_fifth.setOnClickListener(onToggleButtonClickListener)

        button_game_settings_start_game.setOnClickListener {

            if (textview_game_settings_selected_height.text.toString().isEmpty() ||
                textview_game_settings_selected_width.text.toString().isEmpty() ||
                textview_game_settings_selected_mines_count.text.toString().isEmpty()
            ) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.enterAllParamsBeforeStart),
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                val myIntent = Intent(this, MinefieldActivity::class.java)
                myIntent.putExtra(
                    HEIGHT_TAG,
                    textview_game_settings_selected_height.text.toString().toInt()
                )
                myIntent.putExtra(
                    WIDTH_TAG,
                    textview_game_settings_selected_width.text.toString().toInt()
                )
                myIntent.putExtra(
                    MINES_COUNT_TAG,
                    textview_game_settings_selected_mines_count.text.toString().toInt()
                )
                startActivity(myIntent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_toolbar_rules) {
            MainActivity().showGameRulesAlertDialog(this)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSettingSizeDialog() {
        val settingSizeDialog = DialogSettingsSize()
        settingSizeDialog.show(supportFragmentManager, SETTING_SIZE_DIALOG)
    }

    private fun showSettingMinesCountDialog() {
        val settingMinesCountDialog = DialogSettingMinesCount()
        settingMinesCountDialog.show(supportFragmentManager, SETTING_MINES_COUNT_DIALOG)
    }

    private val onToggleButtonClickListener: View.OnClickListener = View.OnClickListener {
        when (it) {
            //the field size configuration buttons
            button_game_settings_size_first -> {
                textview_game_settings_selected_width.text = 8.toString()
                textview_game_settings_selected_height.text = 8.toString()
                button_game_settings_size_second.isChecked = false
                button_game_settings_size_third.isChecked = false
                button_game_settings_size_fourth.isChecked = false
                button_game_settings_size_fifth.isChecked = false
            }
            button_game_settings_size_second -> {
                textview_game_settings_selected_width.text = 10.toString()
                textview_game_settings_selected_height.text = 10.toString()
                button_game_settings_size_first.isChecked = false
                button_game_settings_size_third.isChecked = false
                button_game_settings_size_fourth.isChecked = false
                button_game_settings_size_fifth.isChecked = false
            }
            button_game_settings_size_third -> {
                textview_game_settings_selected_width.text = 16.toString()
                textview_game_settings_selected_height.text = 16.toString()
                button_game_settings_size_first.isChecked = false
                button_game_settings_size_second.isChecked = false
                button_game_settings_size_fourth.isChecked = false
                button_game_settings_size_fifth.isChecked = false
            }
            button_game_settings_size_fourth -> {
                textview_game_settings_selected_width.text = 24.toString()
                textview_game_settings_selected_height.text = 24.toString()
                button_game_settings_size_first.isChecked = false
                button_game_settings_size_second.isChecked = false
                button_game_settings_size_third.isChecked = false
                button_game_settings_size_fifth.isChecked = false
            }
            button_game_settings_size_fifth -> {
                button_game_settings_size_first.isChecked = false
                button_game_settings_size_second.isChecked = false
                button_game_settings_size_third.isChecked = false
                button_game_settings_size_fourth.isChecked = false

                showSettingSizeDialog()
            }
            //the mines count configuration buttons
            button_game_settings_mines_first -> {
                textview_game_settings_selected_mines_count.text = 8.toString()
                button_game_settings_mines_second.isChecked = false
                button_game_settings_mines_third.isChecked = false
                button_game_settings_mines_fourth.isChecked = false
                button_game_settings_mines_fifth.isChecked = false
            }
            button_game_settings_mines_second -> {
                textview_game_settings_selected_mines_count.text = 16.toString()
                button_game_settings_mines_first.isChecked = false
                button_game_settings_mines_third.isChecked = false
                button_game_settings_mines_fourth.isChecked = false
                button_game_settings_mines_fifth.isChecked = false
            }
            button_game_settings_mines_third -> {
                textview_game_settings_selected_mines_count.text = 32.toString()
                button_game_settings_mines_first.isChecked = false
                button_game_settings_mines_second.isChecked = false
                button_game_settings_mines_fourth.isChecked = false
                button_game_settings_mines_fifth.isChecked = false
            }
            button_game_settings_mines_fourth -> {
                textview_game_settings_selected_mines_count.text = 64.toString()
                button_game_settings_mines_first.isChecked = false
                button_game_settings_mines_second.isChecked = false
                button_game_settings_mines_third.isChecked = false
                button_game_settings_mines_fifth.isChecked = false
            }
            button_game_settings_mines_fifth -> {
                button_game_settings_mines_first.isChecked = false
                button_game_settings_mines_second.isChecked = false
                button_game_settings_mines_third.isChecked = false
                button_game_settings_mines_fourth.isChecked = false

                showSettingMinesCountDialog()
            }

        }
    }

    override fun sendSizeParams(width: Int, height: Int) {
        textview_game_settings_selected_width.text = width.toString()
        textview_game_settings_selected_height.text = height.toString()
    }

    override fun sendMinesCount(count: Int) {
        textview_game_settings_selected_mines_count.text = count.toString()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(WIDTH_TAG, textview_game_settings_selected_width.text.toString())
        outState.putString(HEIGHT_TAG, textview_game_settings_selected_height.text.toString())
        outState.putString(
            MINES_COUNT_TAG,
            textview_game_settings_selected_mines_count.text.toString()
        )
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textview_game_settings_selected_width.text = savedInstanceState.getString(WIDTH_TAG)
        textview_game_settings_selected_height.text = savedInstanceState.getString(HEIGHT_TAG)
        textview_game_settings_selected_mines_count.text =
            savedInstanceState.getString(MINES_COUNT_TAG)
    }
}