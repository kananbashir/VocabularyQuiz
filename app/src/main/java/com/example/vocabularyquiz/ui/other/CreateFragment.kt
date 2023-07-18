package com.example.vocabularyquiz.ui.other

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vocabularyquiz.R
import com.example.vocabularyquiz.data.db.UserDatabase
import com.example.vocabularyquiz.data.db.entities.VocabList
import com.example.vocabularyquiz.databinding.FragmentCreateBinding
import com.example.vocabularyquiz.ui.login.UserViewModel
import com.example.vocabularyquiz.ui.login.UserViewModelFactory

class CreateFragment : Fragment() {
    private lateinit var binding: FragmentCreateBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase
    private var addedWordCount: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateBinding.inflate(layoutInflater)
        userDatabase = UserDatabase(requireContext())
        userViewModel = ViewModelProvider(requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)

        binding.addNewWordButtonCreateFrag.setOnClickListener { addNewWordPairLayout() }
        binding.createNewListButtonCreateFrag.setOnClickListener { createNewList() }
        binding.discardButtonCreateFrag.setOnClickListener { discardNewList() }

        return binding.root
    }

    private fun addNewWordPairLayout() {
        binding.totalWordsCreateFrag.text = "Total ${++addedWordCount} word(s) added"
        val wordPairLayout = LayoutInflater.from(requireContext())
            .inflate(R.layout.create_new_list_item_layout, null)

        val layout = binding.newWordsItemContainer

        val deleteButton = wordPairLayout.findViewById<ImageView>(R.id.deleteWordButtonItemA)
        deleteButton.setOnClickListener {
            binding.totalWordsCreateFrag.text = "Total ${--addedWordCount} word(s) added"
            layout.removeView(wordPairLayout)
        }

        layout.addView(wordPairLayout)
    }

    private fun createNewList() {
        var tempWordAzeList: MutableList<String> = mutableListOf()
        var tempWordEngList: MutableList<String> = mutableListOf()
        val listName = binding.addListNameCreateFrag.text.toString()
        val uniqueListName: Boolean = checkListNameUniqueness(listName)
        val parentLayout = binding.newWordsItemContainer

        if (uniqueListName) {
            if (checkAllItemsCompatibility()) {
                tempWordAzeList.add(binding.addWordAzeCreateFrag.text.toString())
                tempWordEngList.add(binding.addWordEngCreateFrag.text.toString())

                for (item in 1 until parentLayout.childCount) {
                    val childLayout = parentLayout.getChildAt(item) as LinearLayout
                    val wordAzeEt = childLayout.findViewById<EditText>(R.id.addWordAzeItem)
                    val wordAze = wordAzeEt.text.toString()
                    val wordEngEt = childLayout.findViewById<EditText>(R.id.addWordEngItem)
                    val wordEng = wordEngEt.text.toString()
                    tempWordAzeList.add(wordAze)
                    tempWordEngList.add(wordEng)
                }

                userViewModel.activeUser.observe(viewLifecycleOwner, Observer { user ->
                    user.vocabList.add(VocabList(listName, tempWordAzeList, tempWordEngList, false))
                    userViewModel.upsert(user)
                })

                findNavController().navigate(CreateFragmentDirections.actionCreateFragmentToMainSession())
            } else {
                Toast.makeText(requireContext(), "Please, fill all the fields!", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(requireContext(), "This list name ($listName) has already been taken!", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun checkAllItemsCompatibility(): Boolean {
        val initialWordAze = binding.addWordAzeCreateFrag.text.toString()
        val initialWordEng = binding.addWordEngCreateFrag.text.toString()
        val listName = binding.addListNameCreateFrag.text.toString()
        val parentLayout = binding.newWordsItemContainer
        var emptyCount: Int = 0

        if (initialWordAze.isEmpty() || initialWordEng.isEmpty() || listName.isEmpty()) {
            emptyCount++
        }

        for (item in 1 until parentLayout.childCount) {
            val childLayout = parentLayout.getChildAt(item) as LinearLayout
            val wordAzeEt = childLayout.findViewById<EditText>(R.id.addWordAzeItem)
            val wordAze = wordAzeEt.text.toString()
            val wordEngEt = childLayout.findViewById<EditText>(R.id.addWordEngItem)
            val wordEng = wordEngEt.text.toString()

            if (wordAze.isEmpty() || wordEng.isEmpty()) {
                emptyCount++
            }
        }

        if (emptyCount == 0 && parentLayout.childCount > 9) {
            return true
        }

        return false
    }

    private fun checkListNameUniqueness (listName: String): Boolean {
        var isUnique: Boolean = true

        userViewModel.activeUser.observe(viewLifecycleOwner, Observer { user ->
            for (i in 0 until user.vocabList.size) {
                if (user.vocabList[i].listTitle == listName){
                    isUnique = false
                    break
                }
            }
        })

        return isUnique
    }

    private fun discardNewList () {
        var dialogBoxBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

        if (checkForFilledColumns()) {
            dialogBoxBuilder.setMessage("Are you sure you want to cancel everything?")
                .setCancelable(true)
                .setPositiveButton("Yes"){_, _ ->
                    findNavController().navigate(CreateFragmentDirections.actionCreateFragmentToMainSession())
                }
                .setNegativeButton("No"){dialogInterface, _ ->
                    dialogInterface.cancel()
                }
                .show()
        } else {
            findNavController().navigate(CreateFragmentDirections.actionCreateFragmentToMainSession())
        }
    }

    private fun checkForFilledColumns (): Boolean {
        val initialWordAze = binding.addWordAzeCreateFrag.text.toString()
        val initialWordEng = binding.addWordEngCreateFrag.text.toString()
        val listName = binding.addListNameCreateFrag.text.toString()
        val parentLayout = binding.newWordsItemContainer
        var isNotEmptyCount: Int = 0

        if (initialWordAze.isNotEmpty() || initialWordEng.isNotEmpty() || listName.isNotEmpty()) {
            isNotEmptyCount++
        }

        for (item in 1 until parentLayout.childCount) {
            val childLayout = parentLayout.getChildAt(item) as LinearLayout
            val wordAzeEt = childLayout.findViewById<EditText>(R.id.addWordAzeItem)
            val wordAze = wordAzeEt.text.toString()
            val wordEngEt = childLayout.findViewById<EditText>(R.id.addWordEngItem)
            val wordEng = wordEngEt.text.toString()

            if (wordAze.isNotEmpty() || wordEng.isNotEmpty()) {
                isNotEmptyCount++
            }
        }

        if (isNotEmptyCount > 0) {
            return true
        }

        return false
    }
}