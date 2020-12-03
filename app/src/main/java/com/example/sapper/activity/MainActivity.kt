package com.example.sapper.activity

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.sapper.R
import com.example.sapper.activity.MinefieldActivity.activity.MinefieldBTActivity
import com.example.sapper.constant.BluetoothConstant
import com.example.sapper.constant.Constant
import com.example.sapper.db.AppDatabase
import com.example.sapper.entity.CompanyGame
import com.example.sapper.entity.CompanyGameDB
import com.example.sapper.entity.Field
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
        setContentView(R.layout.activity_main)

        /** filling DB **/
        /*
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()
        var a:List<CompanyGameDB>
        Thread {
            val dao = db.getCompanyGameDao()
            dao.deleteAll()
            dao.insert(CompanyGameDB(1, 4, 4, 2, 10, 0, false))
            dao.insert(CompanyGameDB(2, 6, 6, 3, 10, 0, false))
            dao.insert(CompanyGameDB(3, 8, 8, 5, 10, 0, false))
            dao.insert(CompanyGameDB(4, 10, 10, 12, 10, 0, false))
            a = dao.getAll()
            Log.e("TAG", "onCreate: $a")
            runOnUiThread(Runnable {
                Toast.makeText(this, "" + a[0], Toast.LENGTH_SHORT).show()
            })
        }.start()
        */
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
            showGameRulesAlertDialog(this)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showGameRulesAlertDialog(context: Context) {
        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.rules)
            .setMessage("R.string.rules? IDK maybe info about game should be placed there ")
            .setPositiveButton(R.string.understand, null)
            .show()

        val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setTextColor(context.resources.getColor(R.color.colorPrimaryDark))
    }


//    fun clickAll(view: View) {
//        val db: SQLiteDatabase = baseContext.openOrCreateDatabase("app.db", MODE_PRIVATE, null)
//        Log.e("TAG", "clickAll: ${DAOCompanyLevel(db).getAllCompanyLevels()}")
//        db.close()
//    }
//
//    fun clickRemove(view: View) {
//        val db: SQLiteDatabase = baseContext.openOrCreateDatabase("app.db", MODE_PRIVATE, null)
//        Log.e("TAG", "clickRemove: ${DAOCompanyLevel(db).removeAllCompanyLevels()}")
//        db.close()
//    }
//    fun clickId(view: View) {
//        val db: SQLiteDatabase = baseContext.openOrCreateDatabase("app.db", MODE_PRIVATE, null)
//        val id = et_test.text.toString().toInt()
//        Log.e("TAG", "clickId: ${DAOCompanyLevel(db).getCompanyLevelById(id)}")
//        db.close()
//    }
//
//    var id = 2
//    fun clickAdd(view: View) {
//        val db: SQLiteDatabase = baseContext.openOrCreateDatabase("app.db", MODE_PRIVATE, null)
//        val obj = CompanyLevel(id,1,1,1,1,1,false)
//        Log.e("TAG", "clickId: ${DAOCompanyLevel(db).insertCompanyLevel(obj)}")
//        id++
//        db.close()
//    }
//
//    fun clickAmount(view: View) {
//        val db: SQLiteDatabase = baseContext.openOrCreateDatabase("app.db", MODE_PRIVATE, null)
//        Log.e("TAG", "clickAmount: ${DAOCompanyLevel(db).getTheNumberOfRecords()}")
//        db.close()
//    }

}


