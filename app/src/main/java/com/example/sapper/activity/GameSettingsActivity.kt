package com.example.sapper.activity

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sapper.Constant
import com.example.sapper.R
import com.example.sapper.dialog.DialogSettingMinesCount
import com.example.sapper.dialog.DialogSettingsSize
import kotlinx.android.synthetic.main.activity_game_settings.*
import kotlinx.android.synthetic.main.new_activity_game_settings.*


class GameSettingsActivity : AppCompatActivity(),
    DialogSettingsSize.DialogSettingsSizeListener,
    DialogSettingMinesCount.DialogSettingMinesCountListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_activity_game_settings)

        val mode = if (savedInstanceState == null) {
            intent.getStringExtra(Constant().GAME_MODE)
        } else {
            tv_game_settings_game_time_selected.text =
                savedInstanceState.getString(Constant().GAME_TIME_TAG, "0:0")
            savedInstanceState.getString(Constant().GAME_MODE)
        }
        /*setting game mode note*/
        when (mode) {
            Constant().GAME_MODE_CREATIVE -> {
                tv_game_settings_game_mode.text = getString(R.string.singleplayer)
                ll_game_settings_miner.visibility = View.GONE
                ll_game_settings_exit.visibility = View.GONE
            }
            Constant().GAME_MODE_BLUETOOTH -> {
                tv_game_settings_game_mode.text = getString(R.string.bluetooth)
            }
        }

        configureSpinners()

        /*Time picker button*/
        btn_game_settings_game_time.setOnClickListener {
            showTimePickerDialog()
        }

        /*setting click listener for button Create */
        btn_game_settings_create_game.setOnClickListener {
            val myIntent = if (mode == Constant().GAME_MODE_BLUETOOTH) {
                Intent(this, WaitingRoomActivity::class.java)
            } else Intent(this, MinefieldActivity::class.java)
            val sizeParams = spin_game_settings_field_size.selectedItem.toString()
            myIntent.putExtra(
                Constant().GAME_MODE,
                mode
            )
            myIntent.putExtra(
                Constant().HEIGHT_TAG,
                sizeParams.substringBeforeLast('*').toInt()
            )
            myIntent.putExtra(
                Constant().WIDTH_TAG,
                sizeParams.substringAfterLast('*').toInt()
            )
            myIntent.putExtra(
                Constant().MINES_COUNT_TAG,
                spin_game_settings_mines_count.selectedItem.toString().toInt()
            )
            myIntent.putExtra(
                Constant().GAME_TIME_TAG,
                tv_game_settings_game_time_selected.text.toString()
            )
            myIntent.putExtra(
                Constant().FIRST_CLICK_MINE_TAG,
                cb_game_settings_first_click_mine.isChecked
            )
            if (mode == Constant().GAME_MODE_BLUETOOTH) {
                myIntent.putExtra(
                    Constant().WHO_MINER_TAG,
                    spin_game_settings_miner.selectedItem.toString()
                )
                myIntent.putExtra(
                    Constant().CLOSE_AFTER_GAME_TAG,
                    cb_game_settings_exit.isChecked
                )
            }
            startActivity(myIntent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Constant().GAME_MODE, tv_game_settings_game_mode.text.toString())
        outState.putString(
            Constant().GAME_TIME_TAG,
            tv_game_settings_game_time_selected.text.toString()
        )
    }

    private fun configureSpinners() {
        /*mine field size*/
        val adapterGameSettingsSizeSelection: ArrayAdapter<String> = ArrayAdapter(
            this, R.layout.spinner_layout_game_settings,
            R.id.textview_spinner_layout_text, resources.getStringArray(R.array.fieldSizes)
        )
        spin_game_settings_field_size.adapter = adapterGameSettingsSizeSelection
        spin_game_settings_field_size.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, itemSelected: View?,
                    selectedItemPosition: Int, selectedId: Long
                ) {
                    when (selectedItemPosition) {
                        5 -> showSettingSizeDialog()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        /*mines count*/
        val adapterGameSettingsMinesCountSelection: ArrayAdapter<String> = ArrayAdapter(
            this, R.layout.spinner_layout_game_settings,
            R.id.textview_spinner_layout_text, resources.getStringArray(R.array.fieldMinesCount)
        )
        spin_game_settings_mines_count.adapter = adapterGameSettingsMinesCountSelection
        spin_game_settings_mines_count.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, itemSelected: View?,
                    selectedItemPosition: Int, selectedId: Long
                ) {
                    when (selectedItemPosition) {
                        5 -> showSettingMinesCountDialog()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        /*miner role*/
        val adapterGameSettingsMinerSelection: ArrayAdapter<String> = ArrayAdapter(
            this, R.layout.spinner_layout_game_settings,
            R.id.textview_spinner_layout_text, resources.getStringArray(R.array.minerRole)
        )
        spin_game_settings_miner.adapter = adapterGameSettingsMinerSelection
    }

    /*option menu*/
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

    /*callbacks from dialogs*/
    override fun sendSizeParams(width: Int, height: Int) {
        textview_game_settings_selected_width.text = width.toString()
        textview_game_settings_selected_height.text = height.toString()
    }

    override fun sendMinesCount(count: Int) {
        textview_game_settings_selected_mines_count.text = count.toString()
    }

    /*showing dialogs*/
    private fun showSettingSizeDialog() {
        val settingSizeDialog = DialogSettingsSize()
        settingSizeDialog.show(supportFragmentManager, Constant().SETTING_SIZE_DIALOG)
    }

    private fun showSettingMinesCountDialog() {
        val settingMinesCountDialog = DialogSettingMinesCount()
        settingMinesCountDialog.show(supportFragmentManager, Constant().SETTING_MINES_COUNT_DIALOG)
    }

    private fun showTimePickerDialog() {
        val hour = 0
        val minute = 0
        val callBack =
            OnTimeSetListener { view, hourOfDay, minute ->
                tv_game_settings_game_time_selected.text =
                    "$hourOfDay:$minute"
            }
        val timePickerDialog = TimePickerDialog(
            this,
            android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            callBack,
            hour,
            minute,
            true
        )
        timePickerDialog.setTitle("Choose time:")
        timePickerDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        timePickerDialog.show()
    }
}