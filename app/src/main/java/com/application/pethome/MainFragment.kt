package com.application.pethome

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.application.pethome.databinding.FragmentLoginBinding
import com.application.pethome.databinding.FragmentMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth


class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.btCerrarSesion.setOnClickListener {
            // Cerrar sesión
            FirebaseAuth.getInstance().signOut()
            // Redirigir a la pantalla de login
            findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
        }

        binding.btPerfil.setOnClickListener {
            // Redirigir a la pantalla de perfil
            findNavController().navigate(R.id.action_mainFragment_to_perfilActivity)
        }
        return binding.root
    }

}