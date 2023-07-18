package com.example.vocabularyquiz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabularyquiz.R

class GamePriorWordsAdapter: RecyclerView.Adapter<GamePriorWordsAdapter.GamePriorViewHolder>() {
    private var wordsList: List<Pair<String, String>> = listOf()

    inner class GamePriorViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var counterItem: TextView = itemView.findViewById(R.id.counterNumPriorListItem)
        var wordItem: TextView = itemView.findViewById(R.id.wordPriorListItem)
        var listTitleItem: TextView = itemView.findViewById(R.id.listTitlePriorListItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamePriorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.word_list_prior_quiz_item, parent, false)
        return GamePriorViewHolder(view)
    }

    override fun getItemCount(): Int {
        return wordsList.size
    }

    override fun onBindViewHolder(holder: GamePriorViewHolder, position: Int) {
        val currentItem = wordsList[position]

        holder.counterItem.text = "${position+1}"
        holder.listTitleItem.text = currentItem.first
        holder.wordItem.text = currentItem.second
    }

    fun setWordsList (wordsList: List<Pair<String, String>>) {
        this.wordsList = wordsList
        notifyDataSetChanged()
    }
}