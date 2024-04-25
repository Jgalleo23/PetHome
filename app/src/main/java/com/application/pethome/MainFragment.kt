package com.application.pethome

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.pethome.databinding.FragmentMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    private lateinit var publicationAdapter: PublicationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        publicationAdapter = PublicationAdapter(listOf())
        binding.rvMascotas.adapter = publicationAdapter
        binding.rvMascotas.layoutManager = LinearLayoutManager(context)

        getPublications()

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

        binding.btAnadir.setOnClickListener() {
            // Redirigir a la pantalla de publicación
            findNavController().navigate(R.id.action_mainFragment_to_publicacionFragment)
        }


        return binding.root
    }

    private fun getPublications() {
        val db = FirebaseFirestore.getInstance()

        db.collectionGroup("publications")
            .get()
            .addOnSuccessListener { documents ->
                val publications = mutableListOf<Publication>()
                for (document in documents) {
                    val publication = document.toObject(Publication::class.java)
                    publications.add(publication)
                }
                publicationAdapter.updatePublications(publications)
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

}