package com.example.atamimessenger.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MessageRoom::class], version = 1)
abstract class MessageDatabase: RoomDatabase() {
    abstract fun getMessageDao(): MessageDao
}