package com.example.sapper.view.activity

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.sapper.model.constant.Constant
import com.example.sapper.model.constant.GameConstant
import com.example.sapper.R
import com.example.sapper.view.activity.MinefieldActivity.activity.MinefieldActivity
import com.example.sapper.view.activity.MinefieldActivity.activity.MinefieldBTActivity
import com.example.sapper.model.constant.BluetoothConstant
import com.example.sapper.dialog.DialogSettingMinesCount
import com.example.sapper.dialog.DialogSettingsSize
import com.example.sapper.model.constant.*
import com.example.sapper.dialog.DialogHelp
import com.example.sapper.model.entity.local.BluetoothGame
import com.example.sapper.model.entity.local.CasualGame
import com.example.sapper.model.entity.local.Field
import com.example.sapper.model.entity.local.Game
import com.example.sapper.view.Utils
import kotlinx.android.synthetic.main.activity_game_settings.*
import java.lang.invoke.MethodHandles.constant


class GameSettingsActivity : AppCompatActivity(),
    DialogSettingsSize.DialogSettingsSizeListener,
    DialogSettingMinesCount.DialogSettingMinesCountListener {

    lateinit var bluetoothAdapter: BluetoothAdapter
    private var mode: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.onActivityCreateSetTheme(this)
        setContentView(R.layout.activity_game_settings)

        np_game_settings_minutes.minValue = 0
        np_game_settings_minutes.maxValue = 59
        np_game_settings_seconds.minValue = 0
        np_game_settings_seconds.maxValue = 59


        mode = if (savedInstanceState == null) {
            intent.getStringExtra(Constant().EXTRA_GAME_MODE)
        } else {
            savedInstanceState.getString(Constant().EXTRA_GAME_MODE)
        }
        /*setting game mode note*/
        when (mode) {
            Constant().EXTRA_GAME_MODE_CREATIVE -> {
                title = getString(R.string.singleplayer)
                ll_game_settings_use_same_field.visibility = View.GONE
            }
            Constant().EXTRA_GAME_MODE_BLUETOOTH -> {
                title = getString(R.string.bluetooth)
                ll_game_settings_first_click_mine.visibility = View.GONE
            }
        }

        configureFieldSizeSpinner()
        configureMinesCountSpinner()

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
        btn_game_settings_create_game.setOnClickListener(createGameClickListener)
    }

    private val createGameClickListener = View.OnClickListener{
        val myIntent = when (mode) {
            Constant().EXTRA_GAME_MODE_BLUETOOTH -> {
                //if bluetooth is turned off - asking to turn it on
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                if (!bluetoothAdapter.isEnabled) {
                    requestEnableBluetooth()
                    return@OnClickListener
                } else Intent(this, MinefieldBTActivity::class.java)
            }
            Constant().EXTRA_GAME_MODE_CREATIVE -> {
                Intent(this, MinefieldActivity::class.java)
            }
            else -> Intent(this, MainActivity::class.java)
        }

        myIntent.putExtra(
            Constant().EXTRA_GAME_MODE,
            mode
        )

        val width = spin_game_settings_field_size.selectedItem
            .toString().substringAfterLast('*').toInt()
        val height = spin_game_settings_field_size.selectedItem
            .toString().substringBeforeLast('*').toInt()
        val minesCount = spin_game_settings_mines_count.selectedItem
            .toString().toInt()
        val timeMin = np_game_settings_minutes.value
        val timeSec = np_game_settings_seconds.value
        val firstClickMine = cb_game_settings_first_click_mine.isChecked
        val sameField = cb_game_settings_use_same_field.isChecked

        var game: Game? = null
        when(mode){
            Constant().EXTRA_GAME_MODE_CREATIVE -> {
                game = CasualGame(Field(width,height,minesCount), timeMin,timeSec, firstClickMine)
            }
            Constant().EXTRA_GAME_MODE_BLUETOOTH -> {
                game = BluetoothGame(Field(width,height,minesCount), timeMin,timeSec, sameField)
                myIntent.putExtra(Constant().EXTRA_BLUETOOTH_ROLE, Constant().ROLE_SERVER)
            }
        }
        myIntent.putExtra(GameConstant().EXTRA_GAME_OBJECT, game)

        startActivity(myIntent)
        finish()
    }

    private fun requestEnableBluetooth() {
        val enableIntent = Intent(
            BluetoothAdapter.ACTION_REQUEST_ENABLE
        )
        startActivityForResult(
            enableIntent,
            BluetoothConstant.REQUEST_ENABLE_BT
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(
            Constant().EXTRA_GAME_MODE,
            title.toString()
        )
    }

    private fun configureFieldSizeSpinner() {
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
    }

    private fun configureMinesCountSpinner() {
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
            showGameRulesAlertDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showGameRulesAlertDialog() {
        val dialog = DialogHelp()
        dialog.show(supportFragmentManager, Constant().HELPER_DIALOG)
    }

    /*callbacks from dialogs*/
    override fun sendSizeParams(width: Int, height: Int) {
        val adapterGameSettingsSizeSelection: ArrayAdapter<String> = ArrayAdapter(
            this, R.layout.spinner_layout_game_settings,
            R.id.textview_spinner_layout_text, arrayListOf("$width*$height")
        )
        spin_game_settings_field_size.adapter = adapterGameSettingsSizeSelection

        spin_game_settings_field_size.setOnTouchListener { v, event ->
            configureFieldSizeSpinner()
            v.performClick()
            true
        }
    }

    override fun sendMinesCount(count: Int) {
        /*when selected custom variant from spinner - show it*/
        val adapterGameSettingsMinesCountSelection: ArrayAdapter<String> = ArrayAdapter(
            this, R.layout.spinner_layout_game_settings,
            R.id.textview_spinner_layout_text, arrayListOf("$count")
        )
        spin_game_settings_mines_count.adapter = adapterGameSettingsMinesCountSelection

        spin_game_settings_mines_count.setOnTouchListener { v, event ->
            configureMinesCountSpinner()
            v.performClick()
            true
        }
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