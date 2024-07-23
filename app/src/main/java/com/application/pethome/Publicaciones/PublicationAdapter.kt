package com.application.pethome.Publicaciones

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.application.pethome.Objetos.Publication
import com.application.pethome.Objetos.User
import com.application.pethome.R
import com.application.pethome.databinding.PublicationModelBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

@SuppressLint("StaticFieldLeak")
private val db = FirebaseFirestore.getInstance()

class PublicationAdapter(private var publications: List<Publication>) :
    RecyclerView.Adapter<PublicationAdapter.PublicationViewHolder>() {

    inner class PublicationViewHolder(val binding: PublicationModelBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicationViewHolder {
        val binding =
            PublicationModelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PublicationViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PublicationViewHolder, position: Int) {
        // Set initial icon based on whether the user has liked the publication
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val publication = publications[position]
        holder.binding.tvUsuario.text = publication.nombre
        holder.binding.tvNUDescripcion.text = publication.nombre + ": " + publication.descripcion
        Picasso.get().load(publication.imagen).resize(400, 400).centerCrop()
            .into(holder.binding.ivPost)

        db.collection("users").whereEqualTo("nombre", publication.nombre).limit(1).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val fotoPerfilUrl = document.getString("imagen")
                    if (!fotoPerfilUrl.isNullOrEmpty()) {
                        Picasso.get().load(fotoPerfilUrl).resize(100, 100).centerCrop()
                            .into(holder.binding.ivPerfil)
                    } else {
                        holder.binding.ivPerfil.setImageResource(R.drawable.account_icon)
                    }
                }
            }

        holder.binding.ivPerfil.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .whereEqualTo("nombre", publication.nombre)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Toast.makeText(
                            holder.itemView.context,
                            "Usuario no encontrado",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val user = documents.documents[0].toObject(User::class.java)
                        if (user != null) {
                            val bundle = Bundle()
                            bundle.putParcelable("user", user)

                            // Navigate to PUBuscadorFragment and pass the user data
                            it.findNavController().navigate(
                                R.id.action_mainFragment_to_PUBuscadorFragment,
                                bundle
                            )
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error obteniendo documentos: ", exception)
                }
        }

        holder.binding.tvUsuario.setOnClickListener{
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .whereEqualTo("nombre", publication.nombre)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Toast.makeText(
                            holder.itemView.context,
                            "Usuario no encontrado",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val user = documents.documents[0].toObject(User::class.java)
                        if (user != null) {
                            val bundle = Bundle()
                            bundle.putParcelable("user", user)

                            // Navigate to PUBuscadorFragment and pass the user data
                            it.findNavController().navigate(
                                R.id.action_mainFragment_to_PUBuscadorFragment,
                                bundle
                            )
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error obteniendo documentos: ", exception)
                }
        }

        val publicationId = publication.id

        // Check if the user has liked the publication
        db.collection("users").document(userId!!).collection("publications").document(publicationId)
            .collection("likes").document(userId).get().addOnSuccessListener {
                if (it.exists()) {
                    holder.binding.btMeGusta.setImageResource(R.drawable.baseline_favorite_24)
                } else {
                    holder.binding.btMeGusta.setImageResource(R.drawable.like_icon)
                }
            }

        // Set click listener to toggle like status
        holder.binding.btMeGusta.setOnClickListener {
            val publicationRef = db.collection("users").document(userId)
                .collection("publications").document(publication.id)
            publicationRef.collection("likes").document(userId).get().addOnSuccessListener {
                if (it.exists()) {
                    publicationRef.collection("likes").document(userId).delete()
                    holder.binding.btMeGusta.setImageResource(R.drawable.like_icon)
                } else {
                    publicationRef.collection("likes").document(userId)
                        .set(hashMapOf("liked" to true))
                    holder.binding.btMeGusta.setImageResource(R.drawable.baseline_favorite_24)
                }
            }
        }
    }

    override fun getItemCount() = publications.size

    @SuppressLint("NotifyDataSetChanged")
    fun updatePublications(newPublications: List<Publication>) {
        this.publications = newPublications
        notifyDataSetChanged()
    }
}