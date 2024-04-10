package com.application.pethome.Perfil

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.application.pethome.databinding.FragmentPerfilBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class PerfilFragment : Fragment() {
    private var _binding: FragmentPerfilBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)

        FirebaseAuth.getInstance().currentUser?.uid?.let {
            FirebaseFirestore.getInstance().collection("users").document(it).get().addOnSuccessListener {
                if (it.exists()) {
                    binding.txtUsuario.text = it.getString("nombre")
                    binding.tvNombreUsuario.text = it.getString("nombre")
                    binding.tvDescripcion.text = it.getString("descripcion")
                    Picasso.get().load(it.getString("imagen")).into(binding.imageView2)
                }
            }
        }

        return binding.root
    }
}