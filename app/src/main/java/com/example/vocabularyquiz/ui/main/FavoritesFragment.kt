package com.example.vocabularyquiz.ui.main

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vocabularyquiz.R
import com.example.vocabularyquiz.adapter.VocabListFavoritesAdapter
import com.example.vocabularyquiz.data.db.UserDatabase
import com.example.vocabularyquiz.data.db.entities.VocabList
import com.example.vocabularyquiz.databinding.FragmentFavoritesBinding
import com.example.vocabularyquiz.ui.login.UserViewModel
import com.example.vocabularyquiz.ui.login.UserViewModelFactory

class FavoritesFragment : Fragment(), VocabListOnItemClickListener {
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase
    private lateinit var favVocabListAdapter: VocabListFavoritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(layoutInflater)

        userDatabase = UserDatabase(requireContext())
        userViewModel = ViewModelProvider(requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)
        favVocabListAdapter = VocabListFavoritesAdapter(userViewModel)
        binding.recyclerViewFavoritesFrag.adapter = favVocabListAdapter
        binding.recyclerViewFavoritesFrag.layoutManager = LinearLayoutManager(requireContext())
        favVocabListAdapter.setVocabListOnItemClickListener(this)

        binding.logOutIconFavoritesFrag.setOnClickListener { logout() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.activeUser.observe(viewLifecycleOwner, Observer { user ->
            favVocabListAdapter.setVocabList(user.vocabList)
            binding.welcomeTextFavoritesFrag.text = "Welcome, ${user.username}"
            binding.allFavoriteListsTextFavoritesFrag.text = "All favorite lists (${favVocabListAdapter.getFavoritesCount().size})"
        })
    }

    override fun onItemClick(vocabList: VocabList, navLocation: String) {
        userViewModel.setActiveVocabList(vocabList)
        when (navLocation) {
            "details" -> { findNavController().navigate(FavoritesFragmentDirections.actionGlobalDetailsFragment()) }
            "edit" -> { findNavController().navigate(FavoritesFragmentDirections.actionGlobalEditFragment()) }
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

    private fun logout () {
        userViewModel.activeUser.observe(viewLifecycleOwner, Observer {activeUser ->
            userViewModel.makeUserInActive(activeUser)
            findNavController().navigate(HomeFragmentDirections.actionGlobalLogginSession())
        })
    }
}