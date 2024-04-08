package com.application.pethome.Inicio

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.application.pethome.R
import com.application.pethome.databinding.FragmentForgotBinding
import com.application.pethome.databinding.FragmentRegisterBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ForgotFragment : Fragment() {
    private var _binding: FragmentForgotBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotBinding.inflate(inflater, container, false)


        binding.btnIniciarSesion.setOnClickListener() {
            FirebaseAuth.getInstance().sendPasswordResetEmail(binding.etUsuario.text.toString())
                .addOnCompleteListener() {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            context,
                            "Se ha enviado un correo para restablecer la contraseña",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        binding.etUsuario.setError("Correo no registrado")
                    }
                }
        }

        return binding.root
    }
}