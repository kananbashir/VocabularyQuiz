package com.example.vocabularyquiz.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vocabularyquiz.data.db.UserDatabase
import com.example.vocabularyquiz.data.repositories.UserRepository

class UserViewModelFactory(private val userDatabase: UserDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(UserRepository(userDatabase)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}