package com.example.vocabularyquiz.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vocabularyquiz.R
import com.example.vocabularyquiz.adapter.SearchAdapter
import com.example.vocabularyquiz.data.db.UserDatabase
import com.example.vocabularyquiz.data.db.entities.VocabList
import com.example.vocabularyquiz.databinding.FragmentSearchBinding
import com.example.vocabularyquiz.ui.login.UserViewModel
import com.example.vocabularyquiz.ui.login.UserViewModelFactory
import kotlin.io.path.fileVisitor

class SearchFragment : Fragment(), VocabListOnItemClickListener, WordListOnItemClickListener {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        userDatabase = UserDatabase(requireContext())
        userViewModel = ViewModelProvider(requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)
        searchAdapter = SearchAdapter(userViewModel)
        searchAdapter.setSelector("vocabList")
        searchAdapter.setVocabItemClickListener(this)
        searchAdapter.setWordItemClickListener(this)
        binding.recyclerViewResultsSearchFrag.adapter = searchAdapter
        binding.recyclerViewResultsSearchFrag.layoutManager = LinearLayoutManager (requireContext())
        binding.logOutIconSearchFrag.setOnClickListener { logOut() }

        binding.radioGroupSearchFrag.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.checkSearchListSearchFrag -> {
                    searchAdapter.setSelector("vocabList")
                    binding.recyclerViewResultsSearchFrag.adapter = searchAdapter
                    userViewModel.activeUser.observe(viewLifecycleOwner, Observer { user ->
                        searchAdapter.setVocabList(user.vocabList)
                        binding.searchResultsTextSearchFrag.text = "Total ${user.vocabList.size} results found"
                        userViewModel.activeUser.removeObservers(viewLifecycleOwner)
                    })
                }

                R.id.checkSearchFavoritesSearchFrag -> {
                    searchAdapter.setSelector("favoriteVocabList")
                    binding.recyclerViewResultsSearchFrag.adapter = searchAdapter
                    userViewModel.activeUser.observe(viewLifecycleOwner, Observer { user ->
                        searchAdapter.setVocabList(user.vocabList)
                        binding.searchResultsTextSearchFrag.text = "Total ${searchAdapter.getFavoriteVocabSize()} results found"
                        userViewModel.activeUser.removeObservers(viewLifecycleOwner)
                    })
                }

                R.id.checkSearchWordsSearchFrag -> {
                    searchAdapter.setSelector("words")
                    binding.recyclerViewResultsSearchFrag.adapter = searchAdapter
                    userViewModel.activeUser.observe(viewLifecycleOwner, Observer { user ->
                        searchAdapter.setVocabList(user.vocabList)
                        binding.searchResultsTextSearchFrag.text = "Total ${searchAdapter.getWordsSize()} results found"
                        userViewModel.activeUser.removeObservers(viewLifecycleOwner)
                    })
                }
            }
        }

        binding.searchViewSearchFrag.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filteredList (newText!!)
                return true
            }

        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.activeUser.observe(viewLifecycleOwner, Observer { user ->
            searchAdapter.setVocabList(user.vocabList)
            binding.welcomeTextMainScreen.text = "Welcome, ${user.username}"
            binding.searchResultsTextSearchFrag.text = "Total ${user.vocabList.size} results found"
            userViewModel.activeUser.removeObservers(viewLifecycleOwner)
        })

    }

    override fun onItemClick(vocabList: VocabList, navLocation: String) {
        userViewModel.setActiveVocabList(vocabList)
        when (navLocation){
            "details" -> {
                findNavController().navigate(HomeFragmentDirections.actionGlobalDetailsFragment())
            }
            "edit" -> {
                findNavController().navigate(HomeFragmentDirections.actionGlobalEditFragment())
            }
            "delete" -> {
                val dialogBoxBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                dialogBoxBuilder.setMessage("Are you sure you want to delete '${vocabList.listTitle}'?")
                    .setPositiveButton("Yes") {_, _ ->
                        userViewModel.deleteVocabList(vocabList)
                    }
                    .setNegativeButton("No") {dialogInterface, _ ->
                        dialogInterface.cancel()
                    }
                    .show()
            }
        }
    }

    override fun onItemClick(wordPair: Pair<String, String>, vocabListName: String) {
        userViewModel.activeUser.observe(viewLifecycleOwner, Observer {user ->
            for (i in user.vocabList) {
                if (i.listTitle == vocabListName) {
                    userViewModel.setActiveVocabList(i)
                }
            }
        })

        findNavController().navigate(SearchFragmentDirections.actionGlobalDetailsFragment())
    }

    private fun filteredList (key: String) {
        val checkedRadioButtonId = binding.radioGroupSearchFrag.checkedRadioButtonId
        userViewModel.activeUser.observe(viewLifecycleOwner, Observer { user ->
            if (checkedRadioButtonId == R.id.checkSearchListSearchFrag || checkedRadioButtonId == R.id.checkSearchFavoritesSearchFrag) {
                val listOfVocabLists: List<VocabList> = user.vocabList
                val filteredList: MutableList<VocabList> = mutableListOf()
                for (item in listOfVocabLists) {
                    if (item.listTitle.contains(key, true)){
                        filteredList.add(item)
                    }
                }
                searchAdapter.setFilteredVocabList(filteredList)
                binding.searchResultsTextSearchFrag.text = "Total ${filteredList.size} results found"
            } else {
                val listOfWords: List<Pair<String, String>> = getWordList(user.vocabList)
                val filteredList: MutableList<Pair<String, String>> = mutableListOf()
                for (item in listOfWords) {
                    if (item.first.contains(key, true)) {
                        filteredList.add(item)
                    }
                }
                searchAdapter.setFilteredWordsList(filteredList)
                binding.searchResultsTextSearchFrag.text = "Total ${filteredList.size} results found"
            }
        })

    }

    private fun getWordList (listOfVocabList: List<VocabList>): List<Pair<String, String>> {
        val tempWordsList = mutableListOf<Pair<String, String>>()
        for (i in 0 until listOfVocabList.size) {
            for (j in 0 until listOfVocabList[i].wordAze.size){
                tempWordsList.add(Pair ("${listOfVocabList[i].wordAze[j]} - ${listOfVocabList[i].wordEng[j]}", listOfVocabList[i].listTitle))
                Log.i("SEARCH", "SearchAdapter ===> Words set - ${tempWordsList[j]}")
            }
        }
        return tempWordsList
    }

    private fun logOut () {
        userViewModel.activeUser.observe(viewLifecycleOwner, Observer {activeUser ->
            userViewModel.makeUserInActive(activeUser)
            findNavController().navigate(HomeFragmentDirections.actionGlobalLogginSession())
        })
    }
}