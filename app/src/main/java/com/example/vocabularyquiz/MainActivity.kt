package com.example.vocabularyquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.vocabularyquiz.data.db.UserDatabase
import com.example.vocabularyquiz.databinding.ActivityMainBinding
import com.example.vocabularyquiz.ui.login.UserViewModel
import com.example.vocabularyquiz.ui.login.UserViewModelFactory
import com.example.vocabularyquiz.ui.main.HomeFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var userViewModel: UserViewModel
    private lateinit var userDatabase: UserDatabase
    private var onBackPressesCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDatabase = UserDatabase(this)
        userViewModel = ViewModelProvider(this, UserViewModelFactory(userDatabase)).get(UserViewModel::class.java)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fcv) as NavHostFragment
        navController = navHostFragment.navController

        val topLevelDestinations = setOf(
            R.id.homeFragment,
            R.id.searchFragment,
            R.id.favoritesFragment,
            R.id.quizFragment
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in topLevelDestinations) {
                binding.bottomNavViewMainScreen.visibility = View.VISIBLE
                binding.bottomNavViewMainScreen.setupWithNavController(navController)
            } else {

                binding.bottomNavViewMainScreen.visibility = View.GONE
            }
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressesCount++
                GlobalScope.launch (Dispatchers.Main) {
                    when (onBackPressesCount){
                        1 -> {
                            if (navController.currentDestination?.id in topLevelDestinations){
                                navController.navigate(R.id.homeFragment)
                                Toast.makeText(this@MainActivity,"Tap again to log out!",Toast.LENGTH_SHORT).show()
                                delay(2000)
                                onBackPressesCount = 0
                            }
                        }
                        2 -> {
                            if (navController.currentDestination?.id in topLevelDestinations) {
                                userViewModel.activeUser.observe(this@MainActivity, Observer {activeUser ->
                                    userViewModel.makeUserInActive(activeUser)
                                    val loginGraph = navController.navInflater.inflate(R.navigation.main_nav)
                                    navController.graph = loginGraph

                                })
                                onBackPressesCount = 0
                            }
                        }
                    }
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}