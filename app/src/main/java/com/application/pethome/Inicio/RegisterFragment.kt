package com.application.pethome.Inicio


import android.graphics.Paint
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.application.pethome.R
import com.application.pethome.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")
    private lateinit var spinnerSexo: Spinner

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        spinnerSexo = binding.spinnerSexo
        val adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.sexos, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSexo.adapter = adapter

        binding.chkMostrar.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.etContrasena.transformationMethod = null // Mostrar contraseña
            } else {
                binding.etContrasena.transformationMethod =
                    PasswordTransformationMethod.getInstance() // Ocultar contraseña
            }
        }
        binding.etContrasena.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && binding.etContrasena.text.length < 6) {
                binding.etContrasena.error = "La contraseña debe tener al menos 6 caracteres"
            }
        }

        binding.btnRegistrar.setOnClickListener {
            if (binding.etCorreo.text.toString().isEmpty() || binding.etContrasena.text.isEmpty()) {
                Toast.makeText(context, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.etCorreo.text.toString(), binding.etContrasena.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = FirebaseAuth.getInstance().currentUser?.uid
                        val correo = binding.etCorreo.text.toString().trim()
                        val nombre = binding.etUsuario.text.toString().trim()
                        val sexo = spinnerSexo.selectedItem.toString().trim()

                        val userData = hashMapOf(
                            "nombre" to nombre,
                            "correo" to correo,
                            "sexo" to sexo
                        )
                        if (user != null) {
                            db.collection("users").document(user).set(userData).addOnCompleteListener {
                                Toast.makeText(
                                    context,
                                    "Datos registrados correctamente, puede iniciar sesión",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        binding.etCorreo.error = "Ha ocurrido un error"
                    }
                }
            }
        }

        binding.txtISesion.paintFlags = binding.txtISesion.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.txtISesion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        return binding.root
    }
}