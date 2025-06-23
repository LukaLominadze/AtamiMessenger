package com.example.atamimessenger.app

import android.app.Application
import androidx.room.Room
import com.example.atamimessenger.database.MessageDatabase

class App: Application() {
    companion object {
        lateinit var instance: App
            private set
    }
    lateinit var db: MessageDatabase

    override fun onCreate() {
        super.onCreate()
        instance = this

        db = Room.databaseBuilder(
            applicationContext,
            MessageDatabase::class.java,
            "MESSAGE_DB"
        ).allowMainThreadQueries().build()
    }
}