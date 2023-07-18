package com.example.vocabularyquiz.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vocabularyquiz.data.db.entities.Quiz
import com.example.vocabularyquiz.data.db.entities.VocabList

class QuizViewModel: ViewModel() {

    private val _listOfQuizzes: MutableLiveData<List<Quiz>> = MutableLiveData<List<Quiz>>()
    val listOfQuizzes: LiveData<List<Quiz>> get() = _listOfQuizzes

    private val _listOfWords: MutableLiveData<List<Pair<String, String>>> = MutableLiveData<List<Pair<String, String>>>()
    val listOfWords: LiveData<List<Pair<String, String>>> get() = _listOfWords

    private val _listOfVocabList: MutableLiveData<List<VocabList>> = MutableLiveData<List<VocabList>>()
    val listOfVocabList: LiveData<List<VocabList>> get() = _listOfVocabList

//    private val _resultWrongAnsweredQuizzes: MutableLiveData<MutableMap<Int, Quiz>> = MutableLiveData<MutableMap<Int, Quiz>>()
//    val resultWrongAnsweredQuizzes: LiveData<MutableMap<Int, Quiz>> get() = _resultWrongAnsweredQuizzes
//
//    private val _resultRightAnsweredQuizzes: MutableLiveData<MutableMap<Int, Quiz>> = MutableLiveData<MutableMap<Int, Quiz>>()
//    val resultRightAnsweredQuizzes: LiveData<MutableMap<Int, Quiz>> get() = _resultRightAnsweredQuizzes
//
//    private val _resultNotAnsweredQuizzes: MutableLiveData<MutableMap<Int, Quiz>> = MutableLiveData<MutableMap<Int, Quiz>>()
//    val resultNotAnsweredQuizzes: LiveData<MutableMap<Int, Quiz>> get() = _resultNotAnsweredQuizzes

    private val _gameResults: MutableLiveData<MutableMap<Int, Pair<String, Quiz>>> = MutableLiveData<MutableMap<Int, Pair<String, Quiz>>>()
    val gameResults: LiveData<MutableMap<Int, Pair<String, Quiz>>> get() = _gameResults

    fun createNewQuizListEngToAze (listOfVocabList: List<VocabList>) {
        val listOfQuizList: MutableList<Quiz> = mutableListOf()
        val wordEngList: MutableList<String> = getEngWordsFromVocabLists (listOfVocabList)
        val wordAzeList: MutableList<String> = getAzeWordsFromVocabLists (listOfVocabList)
        val mutualWordLists: List<Pair<MutableList<String>, MutableList<String>>> = listOf(Pair(wordEngList, wordAzeList))

        for (i in 0 until mutualWordLists[0].first.size) {
            var randQuestion: Int = 0
            var isRepeated: Boolean = true
            var isUniqueQuestion: Boolean = true
            var rand1: Int = 0
            var rand2: Int = 0
            var rand3: Int = 0
            var rand4: Int = 0
                while (isRepeated || isUniqueQuestion) {
                    rand1 = (0 until mutualWordLists[0].first.size).random()
                    rand2 = (0 until mutualWordLists[0].first.size).random()
                    rand3 = (0 until mutualWordLists[0].first.size).random()
                    rand4 = (0 until mutualWordLists[0].first.size).random()
                    isRepeated = checkForIteration(rand1, rand2, rand3, rand4)
                    randQuestion = listOf(rand1, rand2, rand3, rand4).shuffled().first()
                    isUniqueQuestion = checkForUniqueQuestion(listOfQuizList, mutualWordLists[0].first[randQuestion], mutualWordLists[0].second[randQuestion])
                }
            listOfQuizList.add(Quiz(wordEngList[randQuestion], wordAzeList[rand1], wordAzeList[rand2], wordAzeList[rand3], wordAzeList[rand4], wordAzeList[randQuestion]))
        }

        _listOfQuizzes.value = listOfQuizList
        _listOfVocabList.value = listOfVocabList
        setListOfEngWords(listOfVocabList)
    }

    fun createNewQuizListAzeToEng (listOfVocabList: List<VocabList>) {
        val listOfQuizList: MutableList<Quiz> = mutableListOf()
        val wordEngList: MutableList<String> = getEngWordsFromVocabLists (listOfVocabList)
        val wordAzeList: MutableList<String> = getAzeWordsFromVocabLists (listOfVocabList)
        val mutualWordLists: List<Pair<MutableList<String>, MutableList<String>>> = listOf(Pair(wordEngList, wordAzeList))

        for (i in 0 until mutualWordLists[0].second.size) {
            var randQuestion: Int = 0
            var isRepeated: Boolean = true
            var isUniqueQuestion: Boolean = true
            var rand1: Int = 0
            var rand2: Int = 0
            var rand3: Int = 0
            var rand4: Int = 0
            while (isRepeated || isUniqueQuestion) {
                rand1 = (0 until mutualWordLists[0].second.size).random()
                rand2 = (0 until mutualWordLists[0].second.size).random()
                rand3 = (0 until mutualWordLists[0].second.size).random()
                rand4 = (0 until mutualWordLists[0].second.size).random()
                isRepeated = checkForIteration(rand1, rand2, rand3, rand4)
                randQuestion = listOf(rand1, rand2, rand3, rand4).shuffled().first()
                isUniqueQuestion = checkForUniqueQuestion(listOfQuizList, mutualWordLists[0].second[randQuestion], mutualWordLists[0].first[randQuestion])
            }
            listOfQuizList.add(Quiz(wordAzeList[randQuestion], wordEngList[rand1], wordEngList[rand2], wordEngList[rand3], wordEngList[rand4], wordEngList[randQuestion]))
        }

        _listOfQuizzes.value = listOfQuizList
        _listOfVocabList.value = listOfVocabList
        setListOfAzeWords(listOfVocabList)
    }

    private fun setListOfEngWords (listOfVocabList: List<VocabList>) {
        val tempWordList: MutableList<Pair<String, String>> = mutableListOf()
        for (i in 0 until listOfVocabList.size) {
            for (j in 0 until listOfVocabList[i].wordEng.size) {
                tempWordList.add(Pair(listOfVocabList[i].listTitle, listOfVocabList[i].wordEng[j]))
            }
        }
        _listOfWords.value = tempWordList
    }

    private fun setListOfAzeWords (listOfVocabList: List<VocabList>) {
        val tempWordList: MutableList<Pair<String, String>> = mutableListOf()
        for (i in 0 until listOfVocabList.size) {
            for (j in 0 until listOfVocabList[i].wordAze.size) {
                tempWordList.add(Pair(listOfVocabList[i].listTitle, listOfVocabList[i].wordAze[j]))
            }
        }
        _listOfWords.value = tempWordList
    }

    private fun checkForUniqueQuestion (listOfQuizList: List<Quiz>, selectedWord: String, correctAnswerWord: String): Boolean {
        for (i in 0 until listOfQuizList.size) {
            if (listOfQuizList[i].question == selectedWord && listOfQuizList[i].correctAnswer == correctAnswerWord) {
                return true
            }
        }
        return false
    }

    private fun checkForIteration (n1: Int, n2: Int, n3: Int, n4: Int): Boolean {
        val numSet = HashSet<Int>()
        val numList = listOf(n1, n2, n3, n4)

        for (num in numList) {
            if (numSet.contains(num)) {
                return true // Found a repeated element
            }
            numSet.add(num)
        }

        return false // No repeated elements found
    }

    private fun getEngWordsFromVocabLists (listOfVocabList: List<VocabList>): MutableList<String> {
        var tempEngWordList: MutableList<String> = mutableListOf()
        for (i in listOfVocabList) {
            for (j in 0 until i.wordEng.size) {
                tempEngWordList.add(i.wordEng[j])
            }
        }
        return tempEngWordList
    }

    private fun getAzeWordsFromVocabLists (listOfVocabList: List<VocabList>): MutableList<String> {
        var tempAzeWordList: MutableList<String> = mutableListOf()
        for (i in listOfVocabList) {
            for (j in 0 until i.wordAze.size) {
                tempAzeWordList.add(i.wordAze[j])
            }
        }
        return tempAzeWordList
    }

    fun setGameResults (gameResults: MutableMap<Int, Pair<String, Quiz>>) {
        this._gameResults.value = gameResults
    }
}