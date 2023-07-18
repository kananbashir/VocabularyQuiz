package com.example.vocabularyquiz.ui.game

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vocabularyquiz.R
import com.example.vocabularyquiz.databinding.FragmentGamePriorBinding
import com.example.vocabularyquiz.databinding.FragmentResultBinding
import com.example.vocabularyquiz.ui.main.QuizViewModel

class ResultFragment : Fragment() {
    private lateinit var binding: FragmentResultBinding
    private lateinit var quizViewModel: QuizViewModel
    private var wrongAnswers: Int = 0
    private var rightAnswers: Int = 0
    private var notAnswered: Int = 0

   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentResultBinding.inflate(layoutInflater)
       quizViewModel = ViewModelProvider(requireActivity()).get(QuizViewModel::class.java)

       setAnswerResultCounts()

       binding.homeButtonResulsFrag.setOnClickListener{
           findNavController().navigate(ResultFragmentDirections.actionResultFragmentToMainSession())
       }

       return binding.root
   }

    private fun setAnswerResultCounts () {
        quizViewModel.gameResults.observe(viewLifecycleOwner, Observer { gameResults ->
            for (i in gameResults) {
                if (i.value.first == "Wrong") {
                    wrongAnswers++
                } else if (i.value.first == "Right") {
                    rightAnswers++
                } else if (i.value.first == "NotAnswered") {
                    notAnswered++
                }
            }
            binding.totalQuestionsResultsFrag.text = "Total questions: ${gameResults.size}"
            binding.wrongAnswersResultsFrag.text = "Wrong answers: $wrongAnswers"
            binding.rightAnswersResultsFrag.text = "Right answers: $rightAnswers"
            binding.notAnsweredResultsFrag.text = "Not answered: $notAnswered"
        })
    }
}