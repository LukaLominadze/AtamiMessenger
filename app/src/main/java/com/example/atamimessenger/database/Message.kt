package com.example.atamimessenger.database

data class Message(
    //var date:  String, // "yyyyMMdd format
    //var time: String, // "HHmmss" format
    var user: String,
    var message: String
)

class FirebaseMessage() {
    val user: String? = null
    val message: String? = null
}