package com.example.vocabularyquiz.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity (tableName = "vocabularyList_list")
data class VocabList(
    var listTitle: String,
    var wordAze: MutableList<String>,
    var wordEng: MutableList<String>,
    var isFavorite: Boolean
) {
    @PrimaryKey (autoGenerate = true)
    var id: Int? = null
}
