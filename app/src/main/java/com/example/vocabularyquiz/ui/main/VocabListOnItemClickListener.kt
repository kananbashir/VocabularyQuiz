package com.example.vocabularyquiz.ui.main

import com.example.vocabularyquiz.data.db.entities.VocabList

interface VocabListOnItemClickListener {

    fun onItemClick (vocabList: VocabList, navLocation: String)

}