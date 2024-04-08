package com.application.pethome.Inicio

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.application.pethome.MainActivity
import com.application.pethome.R
import com.application.pethome.databinding.FragmentLoginBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.txtRegistro.setOnClickListener() {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.txtContOlvidada.setOnClickListener() {
            findNavController().navigate(R.id.action_loginFragment_to_forgotFragment)
        }

        binding.btnIniciarSesion.setOnClickListener() {
            if (binding.etUsuario.text.toString()
                    .isEmpty() || binding.etContrasena.text.isEmpty()
            ) {
                Toast.makeText(context, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    binding.etUsuario.text.toString(),
                    binding.etContrasena.text.toString()
                )
                    .addOnCompleteListener() {
                        if (it.isSuccessful) {
                            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                        } else {
                            binding.etUsuario.setError("Usuario o contraseña incorrectos")
                        }
                    }
            }
        }

        binding.txtRegistro.paintFlags = binding.txtRegistro.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.txtContOlvidada.paintFlags = binding.txtContOlvidada.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        return binding.root
    }
}