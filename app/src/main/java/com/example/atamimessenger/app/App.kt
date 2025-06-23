package com.example.atamimessenger.app

import android.app.Application
import android.graphics.Color
import androidx.room.Room
import com.example.atamimessenger.database.MessageDatabase
import java.security.MessageDigest

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
        val digest = MessageDigest.getInstance("SHA-256").digest(username.toByteArray())

        // Use the first 3 bytes to get RGB values
        val r = digest[0].toInt() and 0xFF
        val g = digest[1].toInt() and 0xFF
        val b = digest[2].toInt() and 0xFF

        val s = 0.18f

        val newR = (r + ((255 - r) * s)).toInt()
        val newG = (g + ((255 - g) * s)).toInt()
        val newB = (b + ((255 - b) * s)).toInt()

        return Color.rgb(newR, newG, newB)
    }
}