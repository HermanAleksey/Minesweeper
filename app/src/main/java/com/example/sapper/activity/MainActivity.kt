package com.example.sapper.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sapper.Constant
import com.example.sapper.R
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
            }
        }.start()

        button_main_company.setOnClickListener {
            startActivity(Intent(this, CompanyLevelActivity::class.java))
        }

        button_main_custom_game.setOnClickListener {
            val intent = Intent(this, GameSettingsActivity::class.java)
            intent.putExtra(Constant().GAME_MODE, Constant().GAME_MODE_CREATIVE)
            startActivity(intent)
        }
//        button_main_multiplayer.setOnClickListener {
//            val intent = Intent(this, GameSettingsActivity::class.java)
//            intent.putExtra(Constant().GAME_MODE, Constant().GAME_MODE_BLUETOOTH)
//            startActivity(intent)
//        }
//        button_main_multiplayer_client.setOnClickListener {
//            /*if bluetooth is turned off -> request to turn on
//            * if already turned on - start new activity for searching game*/
//            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//            if (!bluetoothAdapter.isEnabled) {
//                val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//                startActivityForResult(enableIntent, Constant().REQUEST_ENABLE_BLUETOOTH)
//            } else {
//                val intent = Intent(this, WaitingRoomActivity::class.java)
//                intent.putExtra(Constant().GAME_MODE, Constant().GAME_MODE_BLUETOOTH)
//                intent.putExtra(Constant().BLUETOOTH_ROLE, Constant().ROLE_CLIENT)
//                startActivity(intent)
//            }
//        }
    }

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

    fun showGameRulesAlertDialog(context: Context) {
        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.rules)
            .setMessage(R.string.rulesOfTheGame)
            .setPositiveButton(R.string.understand, null)
            .show()

        val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setTextColor(context.resources.getColor(R.color.colorPrimaryDark))
    }

}


