package com.example.vocabularyquiz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabularyquiz.R
import com.example.vocabularyquiz.data.db.entities.QuizList

class QuizResultsAdapter: RecyclerView.Adapter<QuizResultsAdapter.QuizResulsViewHolder>() {
    private var listOfQuizList: List<QuizList> = listOf()


    inner class QuizResulsViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val vocabListCount: TextView = itemView.findViewById(R.id.totalVocapListNumResultItem)
        val wordCount: TextView = itemView.findViewById(R.id.totalWordsNumResultItem)
        val wrongAnswersCount: TextView = itemView.findViewById(R.id.totalWrongsNumResultItem)
        val rightAnswersCount: TextView = itemView.findViewById(R.id.totalRightsNumResultItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizResulsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.quiz_result_item, parent, false)
        return QuizResulsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfQuizList.size
    }

    override fun onBindViewHolder(holder: QuizResulsViewHolder, position: Int) {
        val currectItem = listOfQuizList[position]

        holder.vocabListCount.text = "${currectItem.listOfVocabList.size} vocabulary list(s)"
        holder.wordCount.text = "Number of words: ${currectItem.gameResult.size}"
        holder.wrongAnswersCount.text = "Wrong: ${(currectItem.gameResult.values.filter { it.first == "Wrong" }).size}"
        holder.rightAnswersCount.text = "Right: ${(currectItem.gameResult.values.filter { it.first == "Right" }).size}"
    }

    fun setQuizLists (listOfQuizList: List<QuizList>) {
        this.listOfQuizList = listOfQuizList
        notifyDataSetChanged()
    }
}