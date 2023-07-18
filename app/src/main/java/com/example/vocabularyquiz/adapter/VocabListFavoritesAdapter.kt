package com.example.vocabularyquiz.adapter

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

class VocabListFavoritesAdapter(private val userViewModel: UserViewModel): RecyclerView.Adapter<VocabListFavoritesAdapter.VocabListFavoritesViewHolder>() {
    private var listOfFavoriteVocabList: List<VocabList> = listOf()
    private var listOfVocabList: List<VocabList> = listOf()
    private var vocabListOnItemClickListener: VocabListOnItemClickListener? = null

    inner class VocabListFavoritesViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val titleVocabListItem: TextView = itemView.findViewById(R.id.vocabListTitleVocabListItem)
        val nonFavoriteIconVocabListItem: ImageView = itemView.findViewById(R.id.nonFavoriteButtonVocabListItem)
        val editButton: ImageButton = itemView.findViewById(R.id.editButtonVocabListItem)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButtonVocabListItem)
        val detailsButton: ImageButton = itemView.findViewById(R.id.detailsButtonVocabListItem)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VocabListFavoritesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vocab_list_item, parent, false)
        return VocabListFavoritesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfFavoriteVocabList.size
    }

    override fun onBindViewHolder(holder: VocabListFavoritesViewHolder, position: Int) {
        val currentItem = listOfFavoriteVocabList[position]

        holder.titleVocabListItem.text = currentItem.listTitle
        holder.nonFavoriteIconVocabListItem.setImageResource(setFavoriteIcon(currentItem))
        holder.nonFavoriteIconVocabListItem.setOnClickListener { updateFavoriteStatus(currentItem) }
        holder.detailsButton.setOnClickListener { vocabListOnItemClickListener?.onItemClick(currentItem, "details") }
        holder.editButton.setOnClickListener { vocabListOnItemClickListener?.onItemClick(currentItem, "edit") }
        holder.deleteButton.setOnClickListener {
            vocabListOnItemClickListener?.onItemClick(currentItem, "delete")
            notifyItemChanged(position)
        }
    }

    fun setVocabList (listOfVocabList: List<VocabList>) {
        val tempFavVocabList: MutableList<VocabList> = mutableListOf()
        this.listOfVocabList = listOfVocabList
        for (vl in this.listOfVocabList) {
            if (vl.isFavorite) {
                tempFavVocabList.add(vl)
            }
        }

        listOfFavoriteVocabList = tempFavVocabList
    }

    private fun setFavoriteIcon (vocabList: VocabList): Int {
        if (vocabList.isFavorite) {
            return R.drawable.ic_favorite_fill
        }
        return R.drawable.ic_favorite_non_fill
        notifyDataSetChanged()
    }

    fun setVocabListOnItemClickListener (listener: VocabListOnItemClickListener) {
        vocabListOnItemClickListener = listener
    }

    private fun updateFavoriteStatus (vocabList: VocabList) {
        userViewModel.changeFavoriteStatus (vocabList)
        notifyDataSetChanged()
    }

    fun getFavoritesCount (): List<VocabList> {
        return listOfFavoriteVocabList
    }
}