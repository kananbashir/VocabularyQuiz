package com.example.vocabularyquiz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabularyquiz.R
import com.example.vocabularyquiz.data.db.entities.VocabList

class VocabListDetailsAdapter: RecyclerView.Adapter<VocabListDetailsAdapter.VocabListDetailsViewHolder>() {
    private var wordList: List<String> = listOf()

    inner class VocabListDetailsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val wordsCounterNum: TextView = itemView.findViewById(R.id.counterNumWordListItem)
        val wordsItemAzeEng: TextView = itemView.findViewById(R.id.wordsAzeEngWordListItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VocabListDetailsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.word_list_details_item, parent, false)
        return VocabListDetailsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return wordList.size
    }

    override fun onBindViewHolder(holder: VocabListDetailsViewHolder, position: Int) {
        val currentItem = wordList[position]

        holder.wordsCounterNum.text = "${position+1}. "
        holder.wordsItemAzeEng.text = currentItem
    }

    fun setWordList (vocabList: VocabList) {
        val tempMutualList: MutableList<String> = mutableListOf()
        for (i in 0 until vocabList.wordAze.size) {
            tempMutualList.add("${vocabList.wordAze[i]} - ${vocabList.wordEng[i]}")
        }

        wordList = tempMutualList
        notifyDataSetChanged()
    }

    fun setFilteredWordList (filteredWordList: List<String>) {
        wordList = filteredWordList
        notifyDataSetChanged()
    }

    fun getWordList (): List<String> {
        return wordList
    }
}