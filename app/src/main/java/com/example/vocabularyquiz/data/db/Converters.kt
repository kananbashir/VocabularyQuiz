package com.example.vocabularyquiz.data.db

import androidx.room.TypeConverter
import com.example.vocabularyquiz.data.db.entities.QuizList
import com.example.vocabularyquiz.data.db.entities.VocabList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromListVocab(value: List<VocabList>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toListVocab (value: String): MutableList<VocabList>? {
        val listType = object : TypeToken<MutableList<VocabList>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromListQuiz (value: MutableList<QuizList>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toListQuiz (value: String): MutableList<QuizList>? {
        val listType = object: TypeToken<MutableList<QuizList>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromStringToList(data: String): MutableList<String> {
        val listType = object : TypeToken<MutableList<String>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun fromListToString(list: MutableList<String>): String {
        return gson.toJson(list)
    }
}