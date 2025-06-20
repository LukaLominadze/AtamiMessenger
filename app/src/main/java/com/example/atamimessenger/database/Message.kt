package com.example.atamimessenger.database

data class Message(
    var date:  String, // "yyyyMMdd format
    var time: String, // "HHmmss" format
    var username: String,
    var message: String
)