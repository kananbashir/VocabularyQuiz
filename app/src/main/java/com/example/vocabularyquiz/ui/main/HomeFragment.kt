package com.example.vocabularyquiz.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vocabularyquiz.adapter.QuizResultsAdapter
import com.example.vocabularyquiz.adapter.VocabListAdapter
import com.example.vocabularyquiz.data.db.UserDatabase
import com.example.vocabularyquiz.data.db.entities.VocabList
import com.example.vocabularyquiz.databinding.FragmentHomeBinding
import com.example.vocabularyquiz.ui.login.UserViewModel
import com.example.vocabularyquiz.ui.login.UserViewModelFactory

class HomeFragment : Fragment(), VocabListOnItemClickListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase
    private lateinit var vocabListAdapter: VocabListAdapter
    private lateinit var quizResulsAdapter: QuizResultsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        userDatabase = UserDatabase(requireContext())
        userViewModel = ViewModelProvider(requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)

        vocabListAdapter = VocabListAdapter(userViewModel)
        binding.recyclerViewVocabListHomeFrag.adapter = vocabListAdapter
        binding.recyclerViewVocabListHomeFrag.layoutManager = LinearLayoutManager(requireContext())
        vocabListAdapter.setOnItemClickListener(this)

        quizResulsAdapter = QuizResultsAdapter()
        binding.recyclerViewQuizzesListHomeFrag.adapter = quizResulsAdapter
        binding.recyclerViewQuizzesListHomeFrag.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.createNewListButtonHomeFrag.setOnClickListener{ findNavController().navigate(HomeFragmentDirections.actionGlobalCreateFragment()) }
        binding.logOutIconHomeFrag.setOnClickListener { logOut() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.activeUser.observe(viewLifecycleOwner, Observer { user ->
            val listOfVocabList = user.vocabList.toList()
            val listOfQuizList = user.quizzesList.toList()
            vocabListAdapter.setVocabList(listOfVocabList)
            quizResulsAdapter.setQuizLists(listOfQuizList)
            binding.textAllVocabListHomeFrag.text = "All vocabulary lists (${listOfVocabList.size})"

            if (user.vocabList.isNotEmpty()) {
                binding.noListTextHomeFrag.visibility = View.INVISIBLE
            } else {
                binding.noListTextHomeFrag.visibility = View.VISIBLE
            }

            if (user.quizzesList.isNotEmpty()) {
                binding.noQuizTextHomeFrag.visibility = View.INVISIBLE
            } else {
                binding.noQuizTextHomeFrag.visibility = View.VISIBLE
            }
        })

        userViewModel.activeUser.observe(viewLifecycleOwner, Observer { user ->
            binding.welcomeTextHomeFrag.text = "Welcome, ${user.username}"
        })
    }

    private fun logOut () {
            userViewModel.activeUser.observe(viewLifecycleOwner, Observer {activeUser ->
                userViewModel.makeUserInActive(activeUser)
                findNavController().navigate(HomeFragmentDirections.actionGlobalLogginSession())
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
}