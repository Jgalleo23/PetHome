package com.application.pethome.Buscador

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.pethome.Mascota
import com.application.pethome.MascotaAdapter
import com.application.pethome.User
import com.application.pethome.databinding.FragmentPUBuscadorBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore

class PUBuscadorFragment : Fragment() {
    private var _binding: FragmentPUBuscadorBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    private lateinit var mascotaAdapter: MascotaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPUBuscadorBinding.inflate(inflater, container, false)

        // Initialize the RecyclerView and its adapter
        mascotaAdapter = MascotaAdapter(listOf())
        binding.rvMascotasUsuarioBUSC.adapter = mascotaAdapter

        // Set a LinearLayoutManager to the RecyclerView
        binding.rvMascotasUsuarioBUSC.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the user from the arguments
        val user: User? = arguments?.getParcelable("user")

        // Use the user data to fill in the fields
        if (user != null) {
            // Replace these with the actual fields in your layout
            binding.tvNombreUsuarioBUSC.text = user.nombre
            binding.tvDescripcionBUSC.text = user.descripcion
            Glide.with(this)
                .load(user.imagen)
                .into(binding.imageView2BUSC)
            binding.txtUsuarioBUSC.text = user.nombre
            getMascotas(user.uid)
            getNumberOfPosts(user.uid)
            getNumberOfFollowed(user.uid)
            getNumberOfFollowers(user.uid)
        }
    }

    private fun getNumberOfPosts(userId: String) {
        if (userId.isNotEmpty()) {
            val db = FirebaseFirestore.getInstance()
            val query = db.collection("users").document(userId)
                .collection("publications")

            query.count().get(AggregateSource.SERVER).addOnSuccessListener { task ->
                binding.tvPublicacionesCuentaBUSC.text = "${task.count}"
            }
        } else {
            // Handle the case where userId is null or empty
        }
    }

    private fun getNumberOfFollowed(userId: String) {
        if (userId.isNotEmpty()) {
            val db = FirebaseFirestore.getInstance()
            val query = db.collection("users").document(userId)
                .collection("seguidos")

            query.count().get(AggregateSource.SERVER).addOnSuccessListener { task ->
                binding.tvSeguidosCuentaBUSC.text = "${task.count}"
            }
        } else {
            // Handle the case where userId is null or empty
        }
    }

    private fun getNumberOfFollowers(userId: String) {
        if (userId.isNotEmpty()) {
            val db = FirebaseFirestore.getInstance()
            val query = db.collection("users").document(userId)
                .collection("seguidores")

            query.count().get(AggregateSource.SERVER).addOnSuccessListener { task ->
                binding.tvSeguidoresCuentaBUSC.text = "${task.count}"
            }
        } else {
            // Handle the case where userId is null or empty
        }
    }

    private fun getMascotas(userId: String) {
        if (userId.isNotEmpty()) {
            FirebaseFirestore.getInstance().collection("users").document(userId)
                .collection("mascotas")
                .get()
                .addOnSuccessListener { documents ->
                    val mascotas = documents.mapNotNull { it.toObject(Mascota::class.java) }
                    mascotaAdapter.updateMascotas(mascotas)
                }
        } else {
            // Handle the case where userId is null or empty
        }
    }
}