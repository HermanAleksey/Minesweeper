package com.example.sapper.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    val CHECKBOX_TAG: String = "com.example.sapper.GameSettings.Checkbox"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //selecting size
        val adapterGameSettingsSizeSelection: ArrayAdapter<String> = ArrayAdapter(
            this, R.layout.spinner_layout_game_settings,
            R.id.textview_spinner_layout_text, resources.getStringArray(R.array.fieldSizes)
        )
        spinner_game_settings_select_size.adapter = adapterGameSettingsSizeSelection
        spinner_game_settings_select_size.onItemSelectedListener =
            object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, itemSelected: View,
                    selectedItemPosition: Int, selectedId: Long
                ) {
                    when (selectedItemPosition) {
                        0 -> setWidthHeightViewText(8, 8)
                        1 -> setWidthHeightViewText(10, 10)
                        2 -> setWidthHeightViewText(14, 14)
                        3 -> setWidthHeightViewText(20, 20)
                        4 -> setWidthHeightViewText(25, 25)
                        5 -> showSettingSizeDialog()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }


        //selecting mines count
        val adapterGameSettingsMinesCountSelection: ArrayAdapter<String> = ArrayAdapter(
            this, R.layout.spinner_layout_game_settings,
            R.id.textview_spinner_layout_text, resources.getStringArray(R.array.fieldMinesCount)
        )
        spinner_game_settings_select_mines_count.adapter = adapterGameSettingsMinesCountSelection
        spinner_game_settings_select_mines_count.onItemSelectedListener =
            object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, itemSelected: View,
                    selectedItemPosition: Int, selectedId: Long
                ) {
                    when (selectedItemPosition) {
                        0 -> setMinesCountViewText(8)
                        1 -> setMinesCountViewText(16)
                        2 -> setMinesCountViewText(24)
                        3 -> setMinesCountViewText(32)
                        4 -> setMinesCountViewText(48)
                        5 -> showSettingSizeDialog()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

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
                myIntent.putExtra(
                    CHECKBOX_TAG,
                    checkbox_game_settings_first_click_mine.isChecked
                    )
                startActivity(myIntent)
            }
        }
    }

    private fun setMinesCountViewText(minesCount: Int) {
        textview_game_settings_selected_mines_count.text = minesCount.toString()
    }

    private fun setWidthHeightViewText(width: Int, height: Int) {
        textview_game_settings_selected_width.text = width.toString()
        textview_game_settings_selected_height.text = height.toString()
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

    override fun sendSizeParams(width: Int, height: Int) {
        textview_game_settings_selected_width.text = width.toString()
        textview_game_settings_selected_height.text = height.toString()
    }

    override fun sendMinesCount(count: Int) {
        textview_game_settings_selected_mines_count.text = count.toString()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(
            WIDTH_TAG,
            textview_game_settings_selected_width.text.toString()
        )
        outState.putString(
            HEIGHT_TAG,
            textview_game_settings_selected_height.text.toString()
        )
        outState.putString(
            MINES_COUNT_TAG,
            textview_game_settings_selected_mines_count.text.toString()
        )
        outState.putBoolean(
            CHECKBOX_TAG,
            checkbox_game_settings_first_click_mine.isChecked
        )
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textview_game_settings_selected_width.text =
            savedInstanceState.getString(WIDTH_TAG)
        textview_game_settings_selected_height.text =
            savedInstanceState.getString(HEIGHT_TAG)
        textview_game_settings_selected_mines_count.text =
            savedInstanceState.getString(MINES_COUNT_TAG)
        checkbox_game_settings_first_click_mine.isChecked =
            savedInstanceState.getBoolean(CHECKBOX_TAG)
    }
}