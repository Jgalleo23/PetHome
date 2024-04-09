package com.application.pethome.Perfil

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.application.pethome.databinding.FragmentPerfilBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class PerfilFragment : Fragment() {
    private var _binding: FragmentPerfilBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)

        FirebaseFirestore.getInstance().collection("usuarios").document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    binding.tvNombre.text = document.get("nombre").toString()
                }
            }

        return binding.root
    }
}