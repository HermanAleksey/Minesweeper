package com.example.sapper.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.sapper.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_main_company.setOnClickListener {

        }

        button_main_custom_game.setOnClickListener {
            startActivity(Intent(this, CustomGameSettingsActivity::class.java))
        }

        button_main_multiplayer.setOnClickListener {

        }

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