package com.application.pethome.Ajustes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.application.pethome.R
import com.application.pethome.databinding.FragmentAjustesBinding
import com.google.firebase.auth.FirebaseAuth

class AjustesFragment : Fragment() {
    private var _binding: FragmentAjustesBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAjustesBinding.inflate(inflater, container, false)

        binding.btCerrarSesion.setOnClickListener {
            // Cerrar sesión
            FirebaseAuth.getInstance().signOut()
            // Redirigir a la pantalla de login
            findNavController().navigate(R.id.action_ajustesFragment_to_loginFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}