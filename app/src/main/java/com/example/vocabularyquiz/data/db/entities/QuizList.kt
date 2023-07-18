package com.example.vocabularyquiz.data.db.entities

import androidx.room.Entity

@Entity(tableName = "quizList_list")
data class QuizList(
    var listOfVocabList: List<VocabList>,
    var listOfQuizList: List<Quiz>,
    var gameResult: Map<Int, Pair<String, Quiz>> //int-index, string-status, quiz-quiz
)
