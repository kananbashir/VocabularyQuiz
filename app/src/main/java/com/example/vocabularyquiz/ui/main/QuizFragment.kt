package com.example.vocabularyquiz.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vocabularyquiz.R
import com.example.vocabularyquiz.adapter.QuizAdapter
import com.example.vocabularyquiz.data.db.UserDatabase
import com.example.vocabularyquiz.data.db.entities.Quiz
import com.example.vocabularyquiz.data.db.entities.VocabList
import com.example.vocabularyquiz.databinding.FragmentQuizBinding
import com.example.vocabularyquiz.ui.login.UserViewModel
import com.example.vocabularyquiz.ui.login.UserViewModelFactory

class QuizFragment : Fragment() {
    private lateinit var binding: FragmentQuizBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase
    private lateinit var quizViewModel: QuizViewModel
    private lateinit var quizAdapter: QuizAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizBinding.inflate(layoutInflater)
        quizViewModel = ViewModelProvider(requireActivity()).get(QuizViewModel::class.java)

        binding.startQuizButtonQuizFrag.setOnClickListener { startNewQuiz() }

        binding.radioGroupQuizFrag.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.customSelectButtonQuizFrag) {
                binding.recyclerViewListsQuizFrag.visibility = View.VISIBLE
                binding.coverForRecyclerViewQuizFrag.visibility = View.INVISIBLE
                binding.onlyFavoriteCheckBoxQuizFrag.visibility = View.VISIBLE
            } else {
                binding.recyclerViewListsQuizFrag.visibility = View.INVISIBLE
                binding.coverForRecyclerViewQuizFrag.visibility = View.VISIBLE
                binding.onlyFavoriteCheckBoxQuizFrag.visibility = View.INVISIBLE
            }
        }

        binding.onlyFavoriteCheckBoxQuizFrag.setOnCheckedChangeListener { _, isChecked ->
            userViewModel.activeUser.observe(viewLifecycleOwner, Observer { user ->
                if (isChecked) {
                    quizAdapter.setVocabList(getFavoriteVocabLists(user.vocabList))
                } else {
                    quizAdapter.setVocabList(user.vocabList)
                }
            })
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userDatabase = UserDatabase(requireContext())
        userViewModel = ViewModelProvider(requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)
        quizAdapter = QuizAdapter()
        binding.recyclerViewListsQuizFrag.adapter = quizAdapter
        binding.recyclerViewListsQuizFrag.layoutManager = LinearLayoutManager(requireContext())

        userViewModel.activeUser.observe(viewLifecycleOwner, Observer { user ->
            quizAdapter.setVocabList(user.vocabList)
            binding.welcomeTextQuizFrag.text = "Welcome, ${user.username}"
        })

    }

    private fun getFavoriteVocabLists (listOfVocabList: List<VocabList>): List<VocabList> {
        val tempListOfFavoriteVocabList: MutableList<VocabList> = mutableListOf()
        for (vl in listOfVocabList) {
            if (vl.isFavorite) {
                tempListOfFavoriteVocabList.add(vl)
            }
        }
        return tempListOfFavoriteVocabList
    }

    private fun createRandomVocabLists(callback: (List<VocabList>) -> Unit) {
        userViewModel.activeUser.observe(viewLifecycleOwner, Observer { user ->
            val vocabList = user.vocabList
            val randomCount: Int = (0 until vocabList.size).random()
            val randomNumContainer = mutableListOf<Int>()
            val listOfRandomSelectedVocabList: MutableList<VocabList> = mutableListOf()

            if (randomCount != 0) {
                for (i in 0 until randomCount) {
                    var randomIndex: Int = (0 until vocabList.size).random()
                    while (randomNumContainer.contains(randomIndex)) {
                        randomIndex = (0 until vocabList.size).random()
                    }
                    randomNumContainer.add(randomIndex)
                }
                for (i in randomNumContainer) {
                    listOfRandomSelectedVocabList.add(vocabList[i])
                }
            } else {
                listOfRandomSelectedVocabList.add(vocabList.random())
            }
            callback(listOfRandomSelectedVocabList)
        })
    }

    private fun startNewQuiz () {
        if (binding.randomSelectButtonQuizFrag.isChecked) {
            createRandomVocabLists { listOfRandomVocabList ->
                if (binding.engToAzeSelectQuizFrag.isChecked) {
                    quizViewModel.createNewQuizListEngToAze(listOfRandomVocabList)
                    findNavController().navigate(QuizFragmentDirections.actionGlobalGameSession())
                } else {
                    quizViewModel.createNewQuizListAzeToEng(listOfRandomVocabList)
                    findNavController().navigate(QuizFragmentDirections.actionGlobalGameSession())
                }
            }
        } else {
            val listOfCheckedVocabList = quizAdapter.getCheckedVocabList()
            if (listOfCheckedVocabList.isNotEmpty()) {
                if (binding.engToAzeSelectQuizFrag.isChecked) {
                    quizViewModel.createNewQuizListEngToAze(listOfCheckedVocabList)
                    findNavController().navigate(QuizFragmentDirections.actionGlobalGameSession())
                } else {
                    quizViewModel.createNewQuizListAzeToEng(listOfCheckedVocabList)
                    findNavController().navigate(QuizFragmentDirections.actionGlobalGameSession())
                }
            } else {
                Toast.makeText(requireContext(), "Select at least 1 list", Toast.LENGTH_SHORT).show()
            }
        }
    }
}