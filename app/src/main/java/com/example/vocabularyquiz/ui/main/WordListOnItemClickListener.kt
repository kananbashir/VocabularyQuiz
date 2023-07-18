package com.example.vocabularyquiz.ui.main

import com.example.vocabularyquiz.data.db.entities.VocabList

interface WordListOnItemClickListener {

    fun onItemClick (wordPair: Pair<String, String>, vocabListName: String)

}