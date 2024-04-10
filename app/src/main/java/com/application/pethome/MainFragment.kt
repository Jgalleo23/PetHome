package com.application.pethome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.application.pethome.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.btPerfil.setOnClickListener {
            // Redirigir a la pantalla de perfil
            findNavController().navigate(R.id.action_mainFragment_to_perfilFragment)
        }

        binding.btAjustes.setOnClickListener {
            // Redirigir a la pantalla de ajustes
            findNavController().navigate(R.id.action_mainFragment_to_ajustesFragment)
        }

        binding.btBuscar.setOnClickListener {
            // Redirigir a la pantalla de buscador
            findNavController().navigate(R.id.action_mainFragment_to_buscadorFragment)
        }
        return binding.root
    }

}