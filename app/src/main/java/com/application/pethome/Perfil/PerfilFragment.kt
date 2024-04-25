package com.application.pethome.Perfil

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.pethome.R
import com.application.pethome.Mascota
import com.application.pethome.MascotaAdapter
import com.application.pethome.Publication
import com.application.pethome.databinding.FragmentPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class PerfilFragment : Fragment() {
    private var _binding: FragmentPerfilBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    private lateinit var mascotaAdapter: MascotaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)

        // Initialize the RecyclerView and its adapter
        mascotaAdapter = MascotaAdapter(listOf())
        binding.rvMascotasUsuario.adapter = mascotaAdapter
        binding.rvMascotasUsuario.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        getNumberOfPosts()
        getNumberOfFollowed()
        getMascotas()

        FirebaseAuth.getInstance().currentUser?.uid?.let {
            FirebaseFirestore.getInstance().collection("users").document(it).get().addOnSuccessListener {
                if (it.exists()) {
                    binding.txtUsuario.text = it.getString("nombre")
                    binding.tvNombreUsuario.text = it.getString("nombre")
                    binding.tvDescripcion.text = it.getString("descripcion")
                    Picasso.get().load(it.getString("imagen")).into(binding.imageView2)

                    // Query the mascotas collection of the current user
                    FirebaseFirestore.getInstance().collection("users").document(it.id).collection("mascotas")
                        .get()
                        .addOnSuccessListener { documents ->
                            val mascotas = documents.mapNotNull { it.toObject(Mascota::class.java) }
                            mascotaAdapter.updateMascotas(mascotas)
                        }
                }
            }
        }

        binding.fabSubirMascota.setOnClickListener {
            findNavController().navigate(R.id.action_perfilFragment_to_regMascotaFragment)
        }

        return binding.root
    }

    private fun getNumberOfPosts() {
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .collection("publications")

        query.count().get(AggregateSource.SERVER).addOnSuccessListener { task ->
            binding.tvPublicacionesCuenta.text = "${task.count}"
        }
    }

    private fun getNumberOfFollowed(){
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .collection("seguidos")

        query.count().get(AggregateSource.SERVER).addOnSuccessListener { task ->
            binding.tvSeguidosCuenta.text = "${task.count}"
        }
    }

    private fun getMascotas() {
        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            FirebaseFirestore.getInstance().collection("users").document(userId).collection("mascotas")
                .get()
                .addOnSuccessListener { documents ->
                    val mascotas = documents.mapNotNull { it.toObject(Mascota::class.java) }
                    mascotaAdapter.updateMascotas(mascotas)
                }
        }
    }

}