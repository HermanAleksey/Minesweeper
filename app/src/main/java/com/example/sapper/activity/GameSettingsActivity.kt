package com.example.sapper.activity

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sapper.constant.Constant
import com.example.sapper.constant.GameConstant
import com.example.sapper.R
import com.example.sapper.dialog.DialogSettingMinesCount
import com.example.sapper.dialog.DialogSettingsSize
import kotlinx.android.synthetic.main.activity_game_settings.*


class GameSettingsActivity : AppCompatActivity(),
    DialogSettingsSize.DialogSettingsSizeListener,
    DialogSettingMinesCount.DialogSettingMinesCountListener {

    var customWidth = 3
    var customHeight = 3
    var customMinesCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_settings)

        np_game_settings_minutes.minValue = 0
        np_game_settings_minutes.maxValue = 59
        np_game_settings_seconds.minValue = 0
        np_game_settings_seconds.maxValue = 59


        val mode = if (savedInstanceState == null) {
            intent.getStringExtra(Constant().GAME_MODE)
        } else {
//            tv_game_settings_game_time_selected.text =
//                savedInstanceState.getString(GameConstant().GAME_TIME_TAG, "00:00")
            savedInstanceState.getString(Constant().GAME_MODE)
        }
        /*setting game mode note*/
        when (mode) {
            Constant().GAME_MODE_CREATIVE -> {
                title = getString(R.string.singleplayer)
                ll_game_settings_use_same_field.visibility = View.GONE
                ll_game_settings_exit.visibility = View.GONE
            }
            Constant().GAME_MODE_BLUETOOTH -> {
                title = getString(R.string.bluetooth)
            }
        }

        configureSpinners()

        /*can't be selected and FirstClickNotMine and UseSameField*/
        cb_game_settings_first_click_mine.setOnClickListener {
            if (cb_game_settings_first_click_mine.isChecked) {
                cb_game_settings_use_same_field.isChecked = false
            }
        }
        cb_game_settings_use_same_field.setOnClickListener {
            if (cb_game_settings_use_same_field.isChecked) {
                cb_game_settings_first_click_mine.isChecked = false
            }
        }

        /*setting click listener for button Create */
        btn_game_settings_create_game.setOnClickListener {
            val myIntent = when (mode) {
                Constant().GAME_MODE_BLUETOOTH -> {
                    //if bluetooth is turned off - asking to turn it on
                    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    if (!bluetoothAdapter.isEnabled) {
                        val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        startActivityForResult(enableIntent, Constant().REQUEST_ENABLE_BLUETOOTH)
                        return@setOnClickListener
                    }
                    Intent(this, WaitingRoomActivity::class.java)
                }
                else -> {
                    Intent(this, MinefieldActivity::class.java)
                }
            }

            myIntent.putExtra(
                Constant().GAME_MODE,
                mode
            )
            /*processing custom params*/
            val mMinesCount = if (
                spin_game_settings_field_size.selectedItemPosition == 5) {
                customMinesCount
            } else {
                spin_game_settings_field_size.selectedItem
                    .toString().toInt()
            }
            val mHeight = if (spin_game_settings_field_size.selectedItemPosition == 5) {
                customHeight
            } else {
                spin_game_settings_field_size.selectedItem
                    .toString().substringBeforeLast('*').toInt()
            }
            val mWidth = if (spin_game_settings_field_size.selectedItemPosition == 5) {
                customWidth
            } else {
                spin_game_settings_field_size.selectedItem
                    .toString().substringAfterLast('*').toInt()
            }
            if (mWidth*mHeight<mMinesCount){
                Toast.makeText(
                    this, resources.getString(
                        R.string.mines_count_cant_be_greater_that_square
                    ), Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            myIntent.putExtra(
                GameConstant().HEIGHT_TAG,
//                sizeParams.substringBeforeLast('*').toInt()
                mHeight
            )
            myIntent.putExtra(
                GameConstant().WIDTH_TAG,
//                sizeParams.substringAfterLast('*').toInt()
                mWidth
            )
            myIntent.putExtra(
                GameConstant().MINES_COUNT_TAG,
//                spin_game_settings_mines_count.selectedItem.toString().toInt()
                mMinesCount
            )

            myIntent.putExtra(
                GameConstant().GAME_TIME_MINUTES_TAG,
                np_game_settings_minutes.value
            )
            myIntent.putExtra(
                GameConstant().GAME_TIME_SECONDS_TAG,
                np_game_settings_seconds.value
            )
            myIntent.putExtra(
                GameConstant().FIRST_CLICK_MINE_TAG,
                cb_game_settings_first_click_mine.isChecked
            )

            if (mode == Constant().GAME_MODE_BLUETOOTH) {
                myIntent.putExtra(
                    Constant().BLUETOOTH_ROLE,
                    Constant().ROLE_SERVER
                )
                myIntent.putExtra(
                    GameConstant().USE_SAME_FIELD_TAG,
                    cb_game_settings_use_same_field.isChecked
                )
                myIntent.putExtra(
                    GameConstant().CLOSE_AFTER_GAME_TAG,
                    cb_game_settings_exit.isChecked
                )
            }
            startActivity(myIntent)
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(
            Constant().GAME_MODE,
            title.toString()
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
    }

    /*option menu*/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_toolbar_rules) {
            showGameRulesAlertDialog(this)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showGameRulesAlertDialog(context: Context) {
        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.rules)
            .setMessage("R.string.navigation what to do with all this settings")
            .setPositiveButton(R.string.understand, null)
            .show()

        val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setTextColor(context.resources.getColor(R.color.colorPrimaryDark))
    }

    /*callbacks from dialogs*/
    override fun sendSizeParams(width: Int, height: Int) {
        customHeight = height
        customWidth = width
    }

    override fun sendMinesCount(count: Int) {
        customMinesCount = count
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
}