package com.example.vocabularyquiz.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vocabularyquiz.adapter.SearchAdapter
import com.example.vocabularyquiz.data.db.entities.Quiz
import com.example.vocabularyquiz.data.db.entities.QuizList
import com.example.vocabularyquiz.data.db.entities.User
import com.example.vocabularyquiz.data.db.entities.VocabList
import com.example.vocabularyquiz.data.repositories.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _activeUser: MutableLiveData<User> = MutableLiveData<User>()
    val activeUser: LiveData<User> get() = _activeUser

    private val _activeVocabList: MutableLiveData<VocabList> = MutableLiveData<VocabList>()
    val activeVocabList: LiveData<VocabList> get() = _activeVocabList

    fun upsert(user: User) = CoroutineScope(Dispatchers.Main).launch {
        repository.upsert(user)
    }

    fun delete(user: User) = CoroutineScope(Dispatchers.Main).launch {
        repository.delete(user)
    }

    fun deleteAllUsers() = CoroutineScope(Dispatchers.Main).launch {
        repository.deleteAllUsers()
    }

    fun getAllUser() = repository.getAllUser()

    fun changeFavoriteStatus (vocabList: VocabList) {
        val user = _activeUser.value!!
        val indexVocabList = user.vocabList.indexOf(vocabList)

        vocabList.isFavorite = !vocabList.isFavorite

        user.vocabList.set(indexVocabList, vocabList)
        upsert(user)
    }

    fun checkUsernameAndPassword(username: String, password: String, userList: List<User>): Boolean {
            for (user in userList) {
                if (user.username == username && user.password == password) {
                    setActiveUser(user)
                    return true
                }
            }

        if (userList.isEmpty()){
            return true
        }
        return false
    }

    fun checkIfUsernameTaken (username: String, userList: List<User>): Boolean {
        if (userList.isEmpty()) {
            return false
        } else {
            for (item in userList) {
                if (item.username.equals(username, true)) {
                    return true
                }
            }
        }
        return false
    }

    fun setActiveVocabList (vocabList: VocabList) {
        _activeVocabList.value = vocabList
    }

    fun updateVocabList (newVocabList: VocabList) {
        val index = _activeUser.value?.vocabList?.indexOf(_activeVocabList.value)
        _activeUser.value?.vocabList?.set(index!!, newVocabList)
        setActiveVocabList(newVocabList)
        upsert(_activeUser.value!!)
    }

    fun makeUserInActive (user: User) {
        user.isOnline = false
        upsert(user)
        makeVocabListNonActive()
    }

    fun makeUserActive(username: String, userList: List<User>) {
        for (i in userList) {
            if (username == i.username) {
                i.isOnline = true
                upsert(i)
            }
        }
    }

    private fun makeVocabListNonActive () {
        _activeVocabList.value = VocabList("", mutableListOf(), mutableListOf(),false)
    }

    fun setActiveUser (user: User) {
        _activeUser.value = user
    }

    fun deleteVocabList (vocabList: VocabList) {
        val user = _activeUser.value!!
        user.vocabList.remove(vocabList)
        upsert(user)
        _activeUser.value = user
    }

    fun addNewGameResult (listOfVocabList: List<VocabList>, listOfQuiz: List<Quiz>, gameResults: MutableMap<Int, Pair<String, Quiz>>) {
        var user = _activeUser.value!!
        user.quizzesList.add(QuizList(listOfVocabList, listOfQuiz, gameResults))
        upsert(user)
    }
}