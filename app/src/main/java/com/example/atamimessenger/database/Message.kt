package com.example.atamimessenger.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class Message(
    var date:  String, // "yyyyMMdd format
    var time: String, // "HHmmss" format
    var user: String,
    var message: String
)

// for loading messages from firebase
class FirebaseMessage() {
    val user: String? = null
    val message: String? = null
}

@Entity(tableName = "MESSAGES")
data class MessageRoom(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    var id: Int,

    @ColumnInfo(name = "DATE")
    var date: String,

    @ColumnInfo(name = "TIME")
    var time: String,

    @ColumnInfo(name = "SENDER")
    var sender: String,

    @ColumnInfo(name = "RECEIVER")
    var receiver: String,

    @ColumnInfo(name = "MESSAGE")
    var message: String
)