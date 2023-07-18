package com.example.vocabularyquiz.ui.game

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
import com.example.vocabularyquiz.R
import com.example.vocabularyquiz.data.db.UserDatabase
import com.example.vocabularyquiz.data.db.entities.Quiz
import com.example.vocabularyquiz.databinding.FragmentGameBinding
import com.example.vocabularyquiz.ui.login.UserViewModel
import com.example.vocabularyquiz.ui.login.UserViewModelFactory
import com.example.vocabularyquiz.ui.main.QuizViewModel

class GameFragment : Fragment() {
    private lateinit var binding: FragmentGameBinding
    private lateinit var quizViewModel: QuizViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase
    private var quizList: List<Quiz> = listOf()
    private var navCount: Int = 0
    private var quizResult: MutableMap<Int, Pair<String, String>> = mutableMapOf() //first - question, second - correctAnswer

   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentGameBinding.inflate(layoutInflater)
       userDatabase = UserDatabase(requireContext())
       userViewModel = ViewModelProvider(requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)
       quizViewModel = ViewModelProvider(requireActivity()).get(QuizViewModel::class.java)

       quizViewModel.listOfQuizzes.observe(viewLifecycleOwner, Observer { quizList ->
           this.quizList = quizList
           binding.progressBarGameFrag.max = quizList.size
           startQuiz()
       })

       binding.nextButtonGameFrag.setOnClickListener{ playQuizNext() }
       binding.previousButtonGameFrag.setOnClickListener{ playQuizPrevious() }
       binding.finishButtonGameFrag.setOnClickListener { finishGame() }

       return binding.root
    }

    private fun startQuiz () {
        binding.previousButtonGameFrag.visibility = View.INVISIBLE
        updateAnswerTexts()
        updateProgressBar()
    }

    private fun playQuizNext () {
        binding.previousButtonGameFrag.visibility = View.VISIBLE

        updateQuizResult()

        navCount++

        if (navCount == quizList.size-1) {
            binding.nextButtonGameFrag.visibility = View.INVISIBLE
        }

        if (quizResult[navCount] == null) {
            binding.radioGroup.clearCheck()
        }

        updateAnswerTexts()
        updateAnswerCheckStatus()
    }

    private fun playQuizPrevious () {
        binding.nextButtonGameFrag.visibility = View.VISIBLE

        updateQuizResult()

        navCount--

        if (navCount == 0) {
            binding.previousButtonGameFrag.visibility = View.INVISIBLE
        }

        if (quizResult[navCount] == null) {
            binding.radioGroup.clearCheck()
        }

        updateAnswerTexts()
        updateAnswerCheckStatus()
    }

    private fun updateQuizResult () {
        if (binding.answer1radioButtonGameFrag.isChecked) {
            quizResult.put(navCount, Pair(binding.wordQuestionGameFrag.text.toString(), binding.answer1radioButtonGameFrag.text.toString()))
            updateProgressBar()
        } else if (binding.answer2radioButtonGameFrag.isChecked) {
            quizResult.put(navCount, Pair(binding.wordQuestionGameFrag.text.toString(), binding.answer2radioButtonGameFrag.text.toString()))
            updateProgressBar()
        }  else if (binding.answer3radioButtonGameFrag.isChecked) {
            quizResult.put(navCount, Pair(binding.wordQuestionGameFrag.text.toString(), binding.answer3radioButtonGameFrag.text.toString()))
            updateProgressBar()
        } else if (binding.answer4radioButtonGameFrag.isChecked) {
            quizResult.put(navCount, Pair(binding.wordQuestionGameFrag.text.toString(), binding.answer4radioButtonGameFrag.text.toString()))
            updateProgressBar()
        }
    }

    private fun updateAnswerCheckStatus () {
        if (binding.answer1radioButtonGameFrag.text.toString() == quizResult[navCount]?.second) {
            Log.i("GameFrag","updateAnswerCheckStatus() - Ans1, NavCount-$navCount, ${binding.answer1radioButtonGameFrag.text} is equal to ${quizResult[navCount]?.second}")
            binding.answer1radioButtonGameFrag.isChecked = true
        } else if (binding.answer2radioButtonGameFrag.text.toString() == quizResult[navCount]?.second) {
            Log.i("GameFrag","updateAnswerCheckStatus() - Ans2, NavCount-$navCount, ${binding.answer1radioButtonGameFrag.text} is equal to ${quizResult[navCount]?.second}")
            binding.answer2radioButtonGameFrag.isChecked = true
        } else if (binding.answer3radioButtonGameFrag.text.toString() == quizResult[navCount]?.second) {
            Log.i("GameFrag","updateAnswerCheckStatus() - Ans3, NavCount-$navCount, ${binding.answer1radioButtonGameFrag.text} is equal to ${quizResult[navCount]?.second}")
            binding.answer3radioButtonGameFrag.isChecked = true
        } else if (binding.answer4radioButtonGameFrag.text.toString() == quizResult[navCount]?.second) {
            Log.i("GameFrag","updateAnswerCheckStatus() - Ans4, NavCount-$navCount, ${binding.answer1radioButtonGameFrag.text} is equal to ${quizResult[navCount]?.second}")
            binding.answer4radioButtonGameFrag.isChecked = true
        }
    }

    private fun updateAnswerTexts () {
        binding.wordQuestionGameFrag.text = quizList[navCount].question.uppercase()
        binding.answer1radioButtonGameFrag.text = quizList[navCount].answer1
        binding.answer2radioButtonGameFrag.text = quizList[navCount].answer2
        binding.answer3radioButtonGameFrag.text = quizList[navCount].answer3
        binding.answer4radioButtonGameFrag.text = quizList[navCount].answer4
    }

    private fun updateProgressBar () {
        binding.progressBarGameFrag.progress = quizResult.size
        binding.progressBarTextGameFrag.text = "${quizList.size}/${quizResult.size}"
    }

    private fun finishGame () {
        updateQuizResult()
        updateProgressBar()

        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Are you sure you want to finish quiz?")
            .setPositiveButton("Yes") {_, _ ->
                val gameResults: MutableMap<Int, Pair<String, Quiz>> = mutableMapOf()

                val answeredIndexes = quizResult.keys
                val notAnsweredIndexes = (0 until quizList.size).filterNot { it in answeredIndexes }

                for (index in answeredIndexes) {
                    val answer = quizResult[index]?.second
                    val quiz = quizList[index]

                    val result = if (answer == quiz.correctAnswer) "Right" else "Wrong"
                    gameResults[index] = Pair(result, quiz)
                }

                for (index in notAnsweredIndexes) {
                    val quiz = quizList[index]
                    gameResults[index] = Pair("NotAnswered", quiz)
                }

                quizViewModel.setGameResults(gameResults)
                quizViewModel.listOfVocabList.observe(viewLifecycleOwner, Observer { vocabList ->
                    userViewModel.addNewGameResult(vocabList, quizList, gameResults)
                })

                findNavController().navigate(GameFragmentDirections.actionGameFragment2ToResultFragment())
            }
            .setNegativeButton("No") {dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .show()

    }
}