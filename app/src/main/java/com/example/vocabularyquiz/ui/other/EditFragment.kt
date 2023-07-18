package com.example.vocabularyquiz.ui.other

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vocabularyquiz.R
import com.example.vocabularyquiz.data.db.UserDatabase
import com.example.vocabularyquiz.data.db.entities.VocabList
import com.example.vocabularyquiz.databinding.FragmentEditBinding
import com.example.vocabularyquiz.ui.login.UserViewModel
import com.example.vocabularyquiz.ui.login.UserViewModelFactory

class EditFragment : Fragment() {
    private lateinit var binding: FragmentEditBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase
    private var initialWordCount: Int = 0
    private var addedWordCount: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(layoutInflater)
        userDatabase = UserDatabase(requireContext())
        userViewModel = ViewModelProvider(requireActivity(), UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)
        inflateAllWordsLayouts()

        binding.addNewWordButtonEditFrag.setOnClickListener { addNewWordPairLayout() }
        binding.saveButtonEdiFrag.setOnClickListener { saveAllChanges() }
        binding.discardButtonEdiFrag.setOnClickListener { discardAllChanges() }

        return binding.root
    }

    private fun inflateAllWordsLayouts() {
        val parentLayout = binding.newWordsItemContainer
        userViewModel.activeVocabList.observe(viewLifecycleOwner, Observer { vocabList ->
                binding.listTitleEditFrag.text = vocabList.listTitle
                binding.editListNameEditFrag.setText(vocabList.listTitle)
                for (i in 0 until vocabList.wordAze.size) {
                    binding.totalWordsEditFrag.text = "Total ${++addedWordCount} word(s) added"
                    val wordPairLayout = LayoutInflater.from(requireContext())
                        .inflate(R.layout.create_new_list_item_layout, null)

                    val deleteButton: ImageButton = wordPairLayout.findViewById(R.id.deleteWordButtonItemA)
                    var wordAze =
                        wordPairLayout.findViewById<EditText?>(R.id.addWordAzeItem)
                    wordAze.setText(vocabList.wordAze[i])

                    var wordEng =
                        wordPairLayout.findViewById<EditText?>(R.id.addWordEngItem)
                    wordEng.setText(vocabList.wordEng[i])


                    deleteButton.setOnClickListener { parentLayout.removeView(wordPairLayout)
                        binding.totalWordsEditFrag.text = "Total ${--addedWordCount} word(s) added"
                    }

                    parentLayout.addView(wordPairLayout)
                }
                initialWordCount = vocabList.wordAze.size
            })
    }

    private fun addNewWordPairLayout() {
            binding.totalWordsEditFrag.text = "Total ${++addedWordCount} word(s) added"
            val wordPairLayout = LayoutInflater.from(requireContext())
                .inflate(R.layout.create_new_list_item_layout, null)
            val parentLayout = binding.newWordsItemContainer
            val deleteButton: ImageButton = wordPairLayout.findViewById(R.id.deleteWordButtonItemA)

            deleteButton.setOnClickListener {
                parentLayout.removeView(wordPairLayout)
                binding.totalWordsEditFrag.text = "Total ${--addedWordCount} word(s) added"
            }

            parentLayout.addView(wordPairLayout)
    }

    private fun saveAllChanges() {
        val listName = binding.editListNameEditFrag.text.toString()
        val uniqueListName: Boolean = checkListNameUniqueness(listName)

        if (uniqueListName) {
            if (checkForEmptyColumns()) {
                val updatedVocabList = getUpdatedVocabList()
                userViewModel.updateVocabList(updatedVocabList)
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Please, fill all the fields!", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(requireContext(), "This list name ($listName) has already been taken!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun checkForEmptyColumns (): Boolean {
        val parentLayout = binding.newWordsItemContainer
        val listName = binding.editListNameEditFrag.text.toString()
        var emptyCount: Int = 0

        if (listName.isEmpty()) {
            emptyCount++
        }

        for (i in 0 until parentLayout.childCount) {
            val childLayout = parentLayout.getChildAt(i) as LinearLayout
            val wordAze: String = childLayout.findViewById<EditText?>(R.id.addWordAzeItem).text.toString()
            val wordEng: String = childLayout.findViewById<EditText?>(R.id.addWordEngItem).text.toString()
            if (wordAze.isEmpty() || wordEng.isEmpty()) {
                emptyCount++
            }
        }

        if (emptyCount == 0 && parentLayout.childCount >= 9){
            return true
        }

        return false
    }

    private fun checkListNameUniqueness(listName: String): Boolean {
        var isUnique: Boolean = true

        userViewModel.activeVocabList.observe(viewLifecycleOwner, Observer { vocabList ->
            if (listName != vocabList.listTitle) {
                userViewModel.activeUser.observe(viewLifecycleOwner, Observer { user ->
                    for (i in 0 until user.vocabList.size) {
                        if (user.vocabList[i].listTitle == listName) {
                            isUnique = false
                            break
                        }
                    }
                })
            } else {
                isUnique = true
            }
        })

        return isUnique
    }

    private fun discardAllChanges() {
        var dialogBoxBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

        if (checkForChangedColumns()) {
            dialogBoxBuilder.setMessage("Are you sure you want to discard all changes?")
                .setPositiveButton("Yes") {_, _ ->
                    findNavController().popBackStack()
                }
                .setNegativeButton("No") {dialogInterface, _ ->
                    dialogInterface.cancel()
                }
                .show()
        } else {
            findNavController().popBackStack()
        }

    }

    private fun checkForChangedColumns (): Boolean {
        val listName = binding.editListNameEditFrag.text.toString()
        val updatedVocabList = getUpdatedVocabList()
        var changedColumns: Int = 0

        userViewModel.activeVocabList.observe(viewLifecycleOwner, Observer { vocabList ->
            if (listName != vocabList.listTitle) {
                changedColumns++
            }
            if (vocabList.wordAze.size == updatedVocabList.wordAze.size || vocabList.wordEng.size == updatedVocabList.wordEng.size) {
                for (i in 0 until vocabList.wordAze.size) {
                    if (updatedVocabList.wordAze != vocabList.wordAze || updatedVocabList.wordEng != vocabList.wordEng) {
                        changedColumns++
                    }
                }
            } else {
                changedColumns++
            }
        })

        if (changedColumns > 0) {
            return true
        }

        return false
    }

    private fun getUpdatedVocabList(): VocabList {
        val parentLayout = binding.newWordsItemContainer
        val listName = binding.editListNameEditFrag.text.toString()
        var isFavorite: Boolean = false
        val tempWordListAze: MutableList<String> = mutableListOf()
        val tempWordListEng: MutableList<String> = mutableListOf()

        userViewModel.activeVocabList.observe(viewLifecycleOwner, Observer { vocabList ->
            isFavorite = vocabList.isFavorite
        })

        for (i in 0 until parentLayout.childCount) {
            val childLayout = parentLayout.getChildAt(i) as LinearLayout
            val wordAze = childLayout.findViewById<EditText>(R.id.addWordAzeItem).text.toString()
            val wordEng = childLayout.findViewById<EditText>(R.id.addWordEngItem).text.toString()
            tempWordListAze.add(wordAze)
            tempWordListEng.add(wordEng)
        }

        return VocabList(listName, tempWordListAze, tempWordListEng, isFavorite)
    }
}