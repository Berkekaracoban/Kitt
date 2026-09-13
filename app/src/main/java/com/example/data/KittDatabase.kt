package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ChatMessageEntity::class], version = 1, exportSchema = false)
abstract class KittDatabase : RoomDatabase() {
  abstract fun kittDao(): KittDao

  companion object {
    @Volatile
    private var INSTANCE: KittDatabase? = null

    fun getDatabase(context: Context): KittDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          KittDatabase::class.java,
          "kitt_database"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
