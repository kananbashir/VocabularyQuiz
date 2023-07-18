package com.example.vocabularyquiz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabularyquiz.R
import com.example.vocabularyquiz.data.db.entities.VocabList

class QuizAdapter: RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {
    private var listOfVocabList: List<VocabList> = listOf()
    private var listOfCheckedVocabList: MutableList<VocabList> = mutableListOf()


    inner class QuizViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val itemTitle: TextView = itemView.findViewById(R.id.vocabListTitleQuizVocabListItem)
        val favoriteStatus: ImageView = itemView.findViewById(R.id.nonFavoriteButtonQuizVocabListItem)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxQuizVocabListItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.quiz_vocab_list_item, parent, false)
        return QuizViewHolder(view)
    }

    override fun getItemCount(): Int {
       return listOfVocabList.size
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val currentItem = listOfVocabList[position]

        holder.itemTitle.text = listOfVocabList[position].listTitle
        holder.favoriteStatus.setImageResource(setFavoriteStatus(currentItem))

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                true -> {
                    listOfCheckedVocabList.add(currentItem)
                }
                false -> {
                    listOfCheckedVocabList.remove(currentItem)
                }
            }
        }
    }

    private fun setFavoriteStatus (vocabList: VocabList): Int {
        if (vocabList.isFavorite) {
            return R.drawable.ic_favorite_fill
        } else {
            return R.drawable.ic_favorite_non_fill
        }
    }

    fun setVocabList (listOfVocabList: List<VocabList>) {
        this.listOfVocabList = listOfVocabList
        notifyDataSetChanged()
    }

    fun getCheckedVocabList (): List<VocabList> {
        return listOfCheckedVocabList
    }
}