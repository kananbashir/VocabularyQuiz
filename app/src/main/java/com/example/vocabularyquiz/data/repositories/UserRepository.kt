package com.example.vocabularyquiz.data.repositories

import com.example.vocabularyquiz.data.db.UserDatabase
import com.example.vocabularyquiz.data.db.entities.User

class UserRepository(private val db: UserDatabase) {

    suspend fun upsert(user: User) = db.getUserDao().upsert(user)

    suspend fun delete (user: User) = db.getUserDao().delete(user)

    suspend fun deleteAllUsers () = db.getUserDao().deleteAllUsers()

    fun getAllUser () = db.getUserDao().getAllUser()

}