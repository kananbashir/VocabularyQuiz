package com.example.vocabularyquiz.ui.login

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vocabularyquiz.data.db.UserDatabase
import com.example.vocabularyquiz.databinding.FragmentSigninBinding

class SigninFragment : Fragment() {
    private lateinit var binding: FragmentSigninBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userDatabase = UserDatabase.invoke(requireContext())
        userViewModel = ViewModelProvider(requireActivity(),UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)

        autoLogin()

        binding = FragmentSigninBinding.inflate(layoutInflater)

        binding.signinButtonSigninFrag.setOnClickListener { performLogin() }
        binding.iconPasswordHintOffSigninFrag.setOnClickListener { hidePassword() }
        binding.iconPasswordHintOnSigninFrag.setOnClickListener { showPassword() }
        binding.navSignUpButtonSigninFrag.setOnClickListener { findNavController().navigate(SigninFragmentDirections.actionSigninFragmentToSignupFragment()) }

        return binding.root
    }

    private fun performLogin () {
        val username = binding.inputUsernameSigninFrag.text.toString()
        val password = binding.inputPasswordSigninFrag.text.toString()
        userViewModel.getAllUser().observe(viewLifecycleOwner, Observer {userList ->
            val userAuthentication = userViewModel.checkUsernameAndPassword(username, password, userList)

            if (userAuthentication){
                userViewModel.makeUserActive(username, userList)
                findNavController().navigate(SigninFragmentDirections.actionSigninFragmentToHomeFragment())
            } else {
                Toast.makeText(requireContext(), "Username or password is incorrect!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun autoLogin () {
        userViewModel.getAllUser().observe(viewLifecycleOwner, Observer {userList ->
            for (user in userList) {
                if (user.isOnline) {
                    userViewModel.setActiveUser(user)
                    findNavController().navigate(SigninFragmentDirections.actionSigninFragmentToHomeFragment())
                }
            }
        })
    }

    private fun hidePassword () {
        binding.inputPasswordSigninFrag.transformationMethod = HideReturnsTransformationMethod.getInstance()
        binding.iconPasswordHintOnSigninFrag.visibility = View.VISIBLE
        binding.iconPasswordHintOffSigninFrag.visibility = View.GONE
    }

    private fun showPassword () {
        binding.inputPasswordSigninFrag.transformationMethod = PasswordTransformationMethod.getInstance()
        binding.iconPasswordHintOnSigninFrag.visibility - View.GONE
        binding.iconPasswordHintOffSigninFrag.visibility = View.VISIBLE
    }
}