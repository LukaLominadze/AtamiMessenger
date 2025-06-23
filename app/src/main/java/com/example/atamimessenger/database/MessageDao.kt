package com.example.atamimessenger.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MessageDao {
    @Query("select * from MESSAGES where ((SENDER = :user1 or SENDER = :user2) and (RECEIVER = :user2 or RECEIVER = :user1))")
    suspend fun getMessagesOfChat(user1: String, user2: String):List<MessageRoom>

    @Query("select * from MESSAGES where (DATE = :date and (SENDER = :user1 or SENDER = :user2) and (RECEIVER = :user2 or RECEIVER = :user1))")
    suspend fun getMessagesOfChatWithDate(date: String, user1: String, user2: String): List<MessageRoom>

    @Query("""select COUNT(*) from MESSAGES
              where (DATE = :date and TIME = :time and
                     SENDER = :user1 and RECEIVER = :user2 and
                     MESSAGE = :message)
        """)
    suspend fun messageExists(date: String, time: String, user1: String, user2: String, message: String): Int

    @Insert
    suspend fun insertMessage(message: MessageRoom)

    @Query("delete from MESSAGES")
    suspend fun clearMessages()
}