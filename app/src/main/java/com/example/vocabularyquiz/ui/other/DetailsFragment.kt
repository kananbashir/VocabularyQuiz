package com.example.vocabularyquiz.ui.other

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.GeneratedAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vocabularyquiz.R
import com.example.vocabularyquiz.adapter.VocabListDetailsAdapter
import com.example.vocabularyquiz.data.db.UserDatabase
import com.example.vocabularyquiz.data.db.entities.VocabList
import com.example.vocabularyquiz.databinding.FragmentDetailsBinding
import com.example.vocabularyquiz.ui.login.UserViewModel
import com.example.vocabularyquiz.ui.login.UserViewModelFactory

class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase
    private lateinit var vocabListDetailsAdapter: VocabListDetailsAdapter


   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentDetailsBinding.inflate(layoutInflater)
       vocabListDetailsAdapter = VocabListDetailsAdapter()
       binding.recyclerViewDetailsFrag.adapter = vocabListDetailsAdapter
       binding.recyclerViewDetailsFrag.layoutManager = LinearLayoutManager(requireContext())
       Log.i("DetailsUpdate", "onCreateView")

       binding.searchViewDetailsFrag.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
           override fun onQueryTextSubmit(query: String?): Boolean {
               return false
           }

           override fun onQueryTextChange(newText: String?): Boolean {
               filteredList (newText!!)
               return true
           }

       })

       binding.fabEditButtonDetailsFrag.setOnClickListener { findNavController().navigate(DetailsFragmentDirections.actionDetailsFragmentToEditFragment()) }

       return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userDatabase = UserDatabase (requireContext())
        userViewModel = ViewModelProvider(requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)

        userViewModel.activeVocabList.observe(viewLifecycleOwner, Observer { vocabList ->
            binding.listTitleDetailsFrag.text = vocabList.listTitle
            vocabListDetailsAdapter.setWordList(vocabList)
            binding.totalWordsDetailsFrag.text = "Total number of words (${vocabListDetailsAdapter.getWordList().size})"
            Log.i("DetailsUpdate", "onViewCreated - vocab list set")
        })

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })
    }

    private fun filteredList (key: String) {
        val filteredList: MutableList<String> = mutableListOf()

        userViewModel.activeVocabList.observe(viewLifecycleOwner, Observer { vocabList ->
            val wordList = getWordList(vocabList)
            for (i in wordList) {
                if (i.contains(key, true)) {
                    filteredList.add(i)
                }
            }

            vocabListDetailsAdapter.setFilteredWordList(filteredList)
        })
    }

    private fun getWordList (vocabList: VocabList): List<String> {
        val tempWordsList = mutableListOf<String>()
        for (i in 0 until vocabList.wordAze.size) {
            tempWordsList.add("${vocabList.wordAze[i]} - ${vocabList.wordEng[i]}")
        }
        return tempWordsList
    }
}