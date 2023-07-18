package com.example.vocabularyquiz.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.vocabularyquiz.data.db.entities.User

@Database(entities = [User::class], version = 7)
@TypeConverters(Converters::class)
abstract class UserDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao

    companion object {

        private var instance: UserDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }


        private fun createDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, UserDatabase::class.java, "UserDB.db")
                .fallbackToDestructiveMigration().build()
    }
}