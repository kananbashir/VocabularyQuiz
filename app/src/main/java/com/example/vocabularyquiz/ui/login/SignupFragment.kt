package com.example.vocabularyquiz.ui.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.vocabularyquiz.R
import com.example.vocabularyquiz.data.db.UserDatabase
import com.example.vocabularyquiz.data.db.entities.QuizList
import com.example.vocabularyquiz.data.db.entities.User
import com.example.vocabularyquiz.data.db.entities.VocabList
import com.example.vocabularyquiz.data.repositories.UserRepository
import com.example.vocabularyquiz.databinding.FragmentSignupBinding

class SignupFragment : Fragment() {
    private lateinit var binding: FragmentSignupBinding
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var userViewModel: UserViewModel
    private lateinit var userRepository: UserRepository
    private lateinit var userDatabase: UserDatabase
    private var imageUri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(layoutInflater)
        userDatabase = UserDatabase(requireContext())
        userRepository = UserRepository(userDatabase)
        userViewModel = ViewModelProvider(requireActivity(),UserViewModelFactory(userDatabase)).get()

        binding.inputUsernameSigninFrag.doOnTextChanged { text, _, _, _ ->
            checkUsernameCompatibility(text)
        }
        binding.inputPasswordAgainSignupFrag.doOnTextChanged { text, _, _, _ -> checkPasswordCompatibility(text) }
        binding.iconPasswordHintOnSigninFrag.setOnClickListener {
            binding.inputPasswordSigninFrag.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.inputPasswordAgainSignupFrag.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.iconPasswordHintOnSigninFrag.visibility = View.GONE
            binding.iconPasswordHintOffSigninFrag.visibility = View.VISIBLE
        }
        binding.iconPasswordHintOffSigninFrag.setOnClickListener {
            binding.inputPasswordSigninFrag.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.inputPasswordAgainSignupFrag.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.iconPasswordHintOffSigninFrag.visibility = View.GONE
            binding.iconPasswordHintOnSigninFrag.visibility = View.VISIBLE
        }
        binding.navSigninButtonSignupFrag.setOnClickListener{
            findNavController().navigate(SignupFragmentDirections.actionSignupFragmentToSigninFragment())
        }
        binding.addPhotoButtonSignupFrag.setOnClickListener { addPhoto() }
        binding.signupButtonSigninFrag.setOnClickListener { performRegister() }

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            if (result.resultCode == Activity.RESULT_OK){
                val data: Intent? = result.data
                imageUri = data?.data
            }

            Glide.with(this).load(imageUri).into(binding.previewImageSignupFrag)
        }

        return binding.root
    }

    private fun checkUsernameCompatibility (text: CharSequence?) {
        if (text!!.contains(" ")){
            binding.inputUsernameSigninFrag.setBackgroundResource(R.drawable.text_input_bkg_error)
            binding.inputUsernameSigninFrag.setTextColor(resources.getColor(R.color.error_red, null))
            Toast.makeText(requireContext(),"Please use correct form of username",Toast.LENGTH_SHORT).show()
            binding.signupButtonSigninFrag.isClickable = false
            binding.signupButtonSigninFrag.setBackgroundColor(resources.getColor(R.color.nonClicable_button, null))
        } else {
            binding.inputUsernameSigninFrag.setBackgroundResource(R.drawable.text_input_bkg)
            binding.inputUsernameSigninFrag.setTextColor(resources.getColor(R.color.gold_yellow, null))
            binding.signupButtonSigninFrag.isClickable = true
            binding.signupButtonSigninFrag.setBackgroundColor(resources.getColor(R.color.gold_yellow, null))
        }
    }

    private fun checkPasswordCompatibility (text: CharSequence?) {
        val password = binding.inputPasswordSigninFrag.text.toString()
        if (binding.inputPasswordAgainSignupFrag.text.toString().isNotEmpty()) {
            if (text.toString() != password) {
                binding.inputPasswordSigninFrag.setBackgroundResource(R.drawable.text_input_bkg_error)
                binding.inputPasswordAgainSignupFrag.setBackgroundResource(R.drawable.text_input_bkg_error)
                binding.signupButtonSigninFrag.isClickable = false
                binding.signupButtonSigninFrag.setBackgroundColor(resources.getColor(R.color.nonClicable_button, null))
            } else if (text.toString() == password){
                binding.inputPasswordSigninFrag.setBackgroundResource(R.drawable.text_input_bkg)
                binding.inputPasswordAgainSignupFrag.setBackgroundResource(R.drawable.text_input_bkg)
                binding.signupButtonSigninFrag.isClickable = true
                binding.signupButtonSigninFrag.setBackgroundColor(resources.getColor(R.color.gold_yellow, null))
            }
        }
    }

    private fun performRegister () {
        val username = binding.inputUsernameSigninFrag.text.toString()
        val password = binding.inputPasswordSigninFrag.text.toString()
        val userPhoto = imageUri.toString()

        if (username.isNotEmpty() && password.isNotEmpty() && imageUri != null) {
                userViewModel.getAllUser().observe(viewLifecycleOwner, Observer { userList ->
                    val uniqueUsername: Boolean =
                        userViewModel.checkIfUsernameTaken(username, userList)

                    if (!uniqueUsername) {
                        userViewModel.upsert(
                            User(
                                username,
                                password,
                                userPhoto,
                                mutableListOf(),
                                mutableListOf(),
                                false
                            )
                        )
                        binding.inputUsernameSigninFrag.text.clear()
                        binding.inputPasswordSigninFrag.text.clear()
                        binding.inputPasswordAgainSignupFrag.text.clear()
                        findNavController().navigate(SignupFragmentDirections.actionSignupFragmentToSigninFragment())
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Please use another username. '$username is already taken'",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please fill up all necessary columns!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    private fun addPhoto () {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }
}