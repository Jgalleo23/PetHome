package com.application.pethome.Inicio

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.application.pethome.MainActivity
import com.application.pethome.R
import com.application.pethome.databinding.FragmentLoginBinding
import com.application.pethome.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")
    private lateinit var spinnerSexo: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        spinnerSexo = binding.spinnerSexo
        val adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.sexos, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSexo.adapter = adapter

        binding.btnRegistrar.setOnClickListener() {
            if (binding.etCorreo.text.toString().isEmpty() || binding.etContrasena.text.isEmpty()) {
                Toast.makeText(context, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.etCorreo.text.toString(), binding.etContrasena.text.toString()
                ).addOnCompleteListener() {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            context, "Se habría registrado correctamente", Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        binding.etCorreo.setError("Correo ya registrado")
                    }
                }
            }


        }

        binding.txtISesion.paintFlags = binding.txtISesion.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.txtISesion.setOnClickListener() {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        return binding.root
    }
}