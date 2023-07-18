package com.example.vocabularyquiz.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabularyquiz.R
import com.example.vocabularyquiz.data.db.entities.VocabList
import com.example.vocabularyquiz.ui.login.UserViewModel
import com.example.vocabularyquiz.ui.main.VocabListOnItemClickListener
import com.example.vocabularyquiz.ui.main.WordListOnItemClickListener

class SearchAdapter(private val userViewModel: UserViewModel): RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    private var listOfVocabList: List<VocabList> = listOf()
    private var listOfFavoriteVocabList: List<VocabList> = listOf()
    private var listOfWords: List<Pair<String, String>> = listOf()
    private var selectedList: String = "vocabList"
    private var vocabListOnItemClickListener: VocabListOnItemClickListener? = null
    private var wordListOnItemClickListener: WordListOnItemClickListener? = null

    inner class SearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view: View
        when (selectedList) {
            "words" -> {
                Log.i("SEARCH", "SearchAdapter ===> onCreateViewHolder - WORDS inflated")
                view = LayoutInflater.from(parent.context).inflate(R.layout.vocab_list_words_item, parent, false)
            }
            else -> {
                Log.i("SEARCH", "SearchAdapter ===> onCreateViewHolder - VOCABLIST or FAVORITEVOCABLIST inflated")
                view = LayoutInflater.from(parent.context).inflate(R.layout.vocab_list_item, parent, false)
            }
        }

        return SearchViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (selectedList == "vocabList") {
            return listOfVocabList.size
        } else if (selectedList == "favoriteVocabList") {
            return listOfFavoriteVocabList.size
        }
        return listOfWords.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        if (selectedList == "vocabList") {
            Log.i("SEARCH", "SearchAdapter ===> onBindViewHolder - 'vocabList'\n\n")
            val currentItem = listOfVocabList[position]
            holder.itemView.findViewById<TextView>(R.id.vocabListTitleVocabListItem).text = currentItem.listTitle

            holder.itemView.findViewById<ImageView>(R.id.nonFavoriteButtonVocabListItem).setImageResource(setFavoriteIcon(currentItem))
            holder.itemView.findViewById<ImageView>(R.id.nonFavoriteButtonVocabListItem).setOnClickListener { updateFavoriteStatus(currentItem, position) }
            holder.itemView.findViewById<ImageButton>(R.id.detailsButtonVocabListItem).setOnClickListener {vocabListOnItemClickListener?.onItemClick(currentItem, "details") }
            holder.itemView.findViewById<ImageButton>(R.id.editButtonVocabListItem).setOnClickListener {vocabListOnItemClickListener?.onItemClick(currentItem, "edit") }
            holder.itemView.findViewById<ImageButton>(R.id.deleteButtonVocabListItem).setOnClickListener {
                vocabListOnItemClickListener?.onItemClick(currentItem, "delete")
                notifyItemChanged(position)
            }
        } else if (selectedList == "favoriteVocabList") {
            Log.i("SEARCH", "SearchAdapter ===> onBindViewHolder - 'favoriteVocabList'\n\n")
            val currentItem = listOfFavoriteVocabList[position]
            holder.itemView.findViewById<TextView>(R.id.vocabListTitleVocabListItem).text = currentItem.listTitle

            holder.itemView.findViewById<ImageView>(R.id.nonFavoriteButtonVocabListItem).setImageResource(setFavoriteIcon(currentItem))
            holder.itemView.findViewById<ImageView>(R.id.nonFavoriteButtonVocabListItem).setOnClickListener { updateFavoriteStatus(currentItem, position) }
            holder.itemView.findViewById<ImageButton>(R.id.detailsButtonVocabListItem).setOnClickListener {vocabListOnItemClickListener?.onItemClick(currentItem, "details") }
            holder.itemView.findViewById<ImageButton>(R.id.editButtonVocabListItem).setOnClickListener {vocabListOnItemClickListener?.onItemClick(currentItem, "edit") }
            holder.itemView.findViewById<ImageButton>(R.id.deleteButtonVocabListItem).setOnClickListener {
                vocabListOnItemClickListener?.onItemClick(currentItem, "delete")
                notifyItemChanged(position)
            }
        } else if (selectedList == "words") {
            Log.i("SEARCH", "SearchAdapter ===> onBindViewHolder - 'words'\n\n")
            val currentItem = listOfWords[position]
            holder.itemView.findViewById<TextView>(R.id.wordsAzeEngVocabListWordItem).text = currentItem.first
            holder.itemView.findViewById<TextView>(R.id.listNameVocabListWordItem).text = currentItem.second
            holder.itemView.findViewById<ImageButton>(R.id.goListButtonVocabListWordItem).setOnClickListener { wordListOnItemClickListener?.onItemClick(currentItem, currentItem.second) }
        }
    }

    fun setVocabList (listOfVocabList: List<VocabList>) {
        this.listOfVocabList = listOfVocabList
        setLists()
    }

    private fun setLists () {
        var position: Int = 0
        val tempFavVocabList = mutableListOf<VocabList>()
        val tempWordsList = mutableListOf<Pair<String, String>>()
        for (i in 0 until listOfVocabList.size) {
            for (j in 0 until listOfVocabList[i].wordAze.size){
                tempWordsList.add(Pair ("${listOfVocabList[i].wordAze[j]} - ${listOfVocabList[i].wordEng[j]}", listOfVocabList[i].listTitle))
                Log.i("SEARCH", "SearchAdapter ===> Words set - ${tempWordsList[j]}")
            }
            if (listOfVocabList[i].isFavorite) {
                tempFavVocabList.add(listOfVocabList[i])
            }
        }
        listOfFavoriteVocabList = tempFavVocabList
        listOfWords = tempWordsList
        Log.i("SEARCH", "SearchAdapter ===> Vocab list size - ${listOfVocabList.size}")
        Log.i("SEARCH", "SearchAdapter ===> Fav vocab list size - ${listOfFavoriteVocabList.size}")
        Log.i("SEARCH", "SearchAdapter ===> Word list - ${listOfWords.size}")
        notifyDataSetChanged()
    }

    fun setSelector (selector: String) {
        Log.i("SEARCH", "SearchAdapter ===> Selected selector is - $selector")
        selectedList = selector
        notifyDataSetChanged()
    }

    private fun updateFavoriteStatus (vocabList: VocabList, position: Int) {
        userViewModel.changeFavoriteStatus (vocabList)
        notifyItemChanged(position)
    }

    fun setVocabItemClickListener (listener: VocabListOnItemClickListener) {
        vocabListOnItemClickListener = listener
    }

    fun setWordItemClickListener (listener: WordListOnItemClickListener) {
        wordListOnItemClickListener = listener
    }

    fun setFilteredVocabList (listOfVocabList: List<VocabList>) {
        this.listOfVocabList = listOfVocabList
        notifyDataSetChanged()
    }
    fun setFilteredWordsList (listOfWords: List<Pair<String, String>>) {
        this.listOfWords = listOfWords
        notifyDataSetChanged()
    }

    private fun setFavoriteIcon (vocabList: VocabList): Int {
        if (vocabList.isFavorite) {
            return R.drawable.ic_favorite_fill
        }
        return R.drawable.ic_favorite_non_fill
        notifyDataSetChanged()
    }

    fun getWordsSize (): Int {
        return listOfWords.size
    }

    fun getFavoriteVocabSize (): Int {
        return listOfFavoriteVocabList.size
    }
}