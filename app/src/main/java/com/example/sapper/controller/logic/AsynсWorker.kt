package com.example.sapper.controller.logic

import android.content.Context
import androidx.room.Room
import com.example.sapper.controller.db.AppDatabase

class AsynсWorker {

    fun setLevelCompleted(context: Context, completedLevel: Int){
        object: Thread() {
            override fun run() {
                super.run()
                val db = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java, "database-name"
                ).build()
                val dao = db.getCompanyGameDao()
                dao.setCompleted(completedLevel)
            }
        }.start()
    }

}