package com.example.atamimessenger.app

import android.app.Application
import android.graphics.Color
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

    fun colorFromUsername(username: String): Int {
        val hash = username.hashCode()
        val hue = (hash % 360 + 360) % 360 // Ensure hue is positive and in 0-359
        val saturation = 0.5f
        val lightness = 0.8f

        return Color.HSVToColor(floatArrayOf(hue.toFloat(), saturation, lightness))
    }
}