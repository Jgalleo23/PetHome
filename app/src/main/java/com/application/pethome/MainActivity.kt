package com.application.pethome

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.application.pethome.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

private var _binding: ActivityMainBinding? = null
private val binding
    get() = _binding!!

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        // Comprobar si el usuario está logueado
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Redirigir al usuario a la pantalla principal
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_loginFragment_to_mainFragment)
        }

        setContentView(view)
    }
}