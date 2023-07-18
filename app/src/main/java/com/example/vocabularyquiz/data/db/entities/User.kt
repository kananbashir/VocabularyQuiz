package com.example.vocabularyquiz.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity (tableName = "user_table")
data class User(
    var username: String,
    var password: String,
    var photo: String,
    var vocabList: MutableList<VocabList>,
    var quizzesList: MutableList<QuizList>,
    var isOnline: Boolean,
) {
    @PrimaryKey (autoGenerate = true)
    var id: Int? = null
}
