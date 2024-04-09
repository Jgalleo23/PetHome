package com.application.pethome.Inicio

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.application.pethome.R
import com.application.pethome.databinding.ActivityLoginBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

private var _binding: ActivityLoginBinding? = null
private val binding
    get() = _binding!!

class LoginActivity : AppCompatActivity() {
    private lateinit var splash: SplashScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splash = installSplashScreen()
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        val navController = navHostFragment?.navController
        if (navController != null) {
            //binding.bottomNav.setupWithNavController(navController)
        }

        setContentView(view)
        splash.setKeepOnScreenCondition {false}
    }
}