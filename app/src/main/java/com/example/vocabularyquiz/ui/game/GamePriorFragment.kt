package com.example.vocabularyquiz.ui.game

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vocabularyquiz.R
import com.example.vocabularyquiz.adapter.GamePriorWordsAdapter
import com.example.vocabularyquiz.data.db.entities.Quiz
import com.example.vocabularyquiz.databinding.FragmentGamePriorBinding
import com.example.vocabularyquiz.ui.main.QuizViewModel

class GamePriorFragment : Fragment() {
    private lateinit var binding: FragmentGamePriorBinding
    private lateinit var quizViewModel: QuizViewModel
    private lateinit var gamePriorWordsAdapter: GamePriorWordsAdapter

   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentGamePriorBinding.inflate(layoutInflater)

       quizViewModel = ViewModelProvider(requireActivity()).get(QuizViewModel::class.java)
       gamePriorWordsAdapter = GamePriorWordsAdapter()
       binding.recyclerViewPriorFrag.adapter = gamePriorWordsAdapter
       binding.recyclerViewPriorFrag.layoutManager = LinearLayoutManager (requireContext())

       quizViewModel.listOfWords.observe(viewLifecycleOwner, Observer { wordList ->
           quizViewModel.listOfQuizzes.observe(viewLifecycleOwner, Observer { quizList ->
               quizViewModel.listOfVocabList.observe(viewLifecycleOwner, Observer { vocabList ->
                   gamePriorWordsAdapter.setWordsList(wordList)
                   binding.infoTextPriorFrag.text = "Total ${quizList.size} questions are going to be answered from ${vocabList.size} vocabulary lists"
                   quizViewModel.listOfVocabList.removeObservers(viewLifecycleOwner)
               })
               quizViewModel.listOfQuizzes.removeObservers(viewLifecycleOwner)
           })
           quizViewModel.listOfWords.removeObservers(viewLifecycleOwner)
       })

       binding.cancelGameButtonPriorFrag.setOnClickListener { cancelQuiz() }
       binding.startGameButtonPriorFrag.setOnClickListener { startQuiz() }

       return binding.root
    }

    private fun cancelQuiz () {
        val dialogBoxBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        dialogBoxBuilder.setMessage("Are you sure you want to cancel quiz?")
            .setPositiveButton("Yes") { _, _ ->
                findNavController().navigate(GamePriorFragmentDirections.actionGamePriorFragmentToMainSession())
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .show()
    }

    private fun startQuiz () {
        findNavController().navigate(GamePriorFragmentDirections.actionGamePriorFragmentToGameFragment2())
    }
}