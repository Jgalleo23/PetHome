package com.application.pethome.Inicio

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.application.pethome.R
import com.application.pethome.databinding.FragmentAuthBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthFragment : Fragment() {
    //Implementa binding
    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            checkEmailVerification()
            handler.postDelayed(this, 2000)
        }
    }

    private lateinit var db: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        checkEmailVerification()
        handler.post(runnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    private fun checkEmailVerification() {
        // Comprobar si el correo electrónico ha sido verificado
        val user = FirebaseAuth.getInstance().currentUser
        user?.reload()?.addOnCompleteListener {
            if (user.isEmailVerified) {
                // Si el correo electrónico ha sido verificado, comprobar si el usuario ha sido verificado
                db.collection("users").document(user.uid).get()
                    .addOnSuccessListener { document ->
                        val isVerified = document.getBoolean("verificado") ?: false
                        if (isVerified) {
                            // Si el usuario ha sido verificado, redirigir al LoginFragment
                            findNavController().navigate(R.id.action_authFragment_to_loginFragment)
                        } else {
                            // Si el usuario no ha sido verificado, mostrar un mensaje
                            Toast.makeText(
                                context,
                                "Por favor, verifica tu cuenta",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                // Si el correo electrónico no ha sido verificado, mostrar un mensaje
                Toast.makeText(
                    context,
                    "Por favor, verifica tu correo electrónico",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bloquear el botón de retroceso
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            // No hacer nada
        }
    }
}