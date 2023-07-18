package com.example.vocabularyquiz.data.db.entities

data class Quiz(
    var question: String,
    var answer1: String,
    var answer2: String,
    var answer3: String,
    var answer4: String,
    var correctAnswer: String
)
