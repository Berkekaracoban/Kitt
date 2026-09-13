package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface KittDao {
  @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
  fun getAllMessages(): Flow<List<ChatMessageEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessage(message: ChatMessageEntity): Long

  @Query("DELETE FROM chat_messages")
  suspend fun clearAllMessages()

  @Query("DELETE FROM chat_messages WHERE id = :id")
  suspend fun deleteMessage(id: Long)
}
