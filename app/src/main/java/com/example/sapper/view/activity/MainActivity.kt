package com.example.sapper.view.activity

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.sapper.R
import com.example.sapper.db.AppDatabase
import com.example.sapper.dialog.DialogHelp
import com.example.sapper.model.ThemeApplication
import com.example.sapper.model.constant.BluetoothConstant
import com.example.sapper.model.constant.Constant
import com.example.sapper.view.Utils
import com.example.sapper.view.activity.MinefieldActivity.activity.MinefieldBTActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object Companion {
        lateinit var context: Context
    }

    private var mBluetoothAdapter: BluetoothAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*theme processing*/
        val sharedPreferences = getSharedPreferences(Constant().APP_PREFERENCES_THEME, MODE_PRIVATE)
        ThemeApplication.currentPosition = sharedPreferences.getInt(Constant().CURRENT_THEME, 0)
        Utils.onActivityCreateSetTheme(this)

        setContentView(R.layout.activity_main)
        setupSpinnerItemSelection()


        /** filling DB with values**/
        val newDB = getPreferences(MODE_PRIVATE).getBoolean("NEW_DB", true)
        val db = AppDatabase.getDatabase(this)
        if (newDB) Thread {
            try {
                db.callOnCreateDatabase(this)
                getPreferences(MODE_PRIVATE).edit().putBoolean("NEW_DB", false)
            } catch (e: Exception) {
                Log.e("TAG", "onCreate: Error on initial filling DB")
            }
        }.start()
        /**-----------------------------**/

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        context = baseContext

        /**------------------------------AdMob------------------------------------------*/
        MobileAds.initialize(this) { }

        val mAdView = findViewById<AdView>(R.id.adView)
        val adRequest: AdRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        Log.e("TAG", "onCreate: ${resources.displayMetrics.densityDpi}")

        button_main_company.setOnClickListener {
            startActivity(Intent(this, CompanyLevelActivity::class.java))
        }

        button_main_custom_game.setOnClickListener {
            val intent = Intent(this, GameSettingsActivity::class.java)
            intent.putExtra(Constant().EXTRA_GAME_MODE, Constant().EXTRA_GAME_MODE_CREATIVE)
            startActivity(intent)
        }

        button_main_bluetooth_game_server.setOnClickListener {
            if (!mBluetoothAdapter!!.isEnabled) {
                requestEnableBluetooth()
            } else {
                val intent = Intent(this, GameSettingsActivity::class.java)
                intent.putExtra(Constant().EXTRA_GAME_MODE, Constant().EXTRA_GAME_MODE_BLUETOOTH)
                intent.putExtra(Constant().EXTRA_BLUETOOTH_ROLE, Constant().ROLE_SERVER)
                startActivity(intent)
            }
        }

        button_main_bluetooth_game_client.setOnClickListener {
            if (!mBluetoothAdapter!!.isEnabled) {
                requestEnableBluetooth()
            } else {
                val intent = Intent(this, MinefieldBTActivity::class.java)
                intent.putExtra(Constant().EXTRA_BLUETOOTH_ROLE, Constant().ROLE_CLIENT)
                startActivity(intent)
            }
        }
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


    private lateinit var spThemes: Spinner
    private fun setupSpinnerItemSelection() {
        spThemes = findViewById<View>(R.id.spThemes) as Spinner
        spThemes.setSelection(ThemeApplication.currentPosition)
        ThemeApplication.currentPosition = spThemes.selectedItemPosition
        spThemes.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?, view: View,
                i: Int, l: Long
            ) {
                if (ThemeApplication.currentPosition != i) {
                    saveCurrentTheme(i)
                    Utils.applySelectedTheme(this@MainActivity)
                }
                ThemeApplication.currentPosition = i
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun saveCurrentTheme(pos: Int) {
        val sharedPreferences = getSharedPreferences(
            Constant().APP_PREFERENCES_THEME,
            MODE_PRIVATE
        )
        sharedPreferences.edit().putInt(Constant().CURRENT_THEME, pos).apply()
    }

}


