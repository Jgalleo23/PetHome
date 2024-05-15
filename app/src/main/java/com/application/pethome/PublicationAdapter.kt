package com.application.pethome

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.pethome.databinding.PublicationModelBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class PublicationAdapter(private var publications: List<Publication>) :
    RecyclerView.Adapter<PublicationAdapter.PublicationViewHolder>() {

    inner class PublicationViewHolder(val binding: PublicationModelBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicationViewHolder {
        val binding =
            PublicationModelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PublicationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PublicationViewHolder, position: Int) {
        val publication = publications[position]
        holder.binding.tvUsuario.text = publication.nombre
        Picasso.get().load(publication.imagen).resize(400, 400).centerCrop()
            .into(holder.binding.ivPost)

        // Set initial icon based on whether the user has liked the publication
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val publicationId = publication.id

        // Set click listener to toggle like status
        holder.binding.btMeGusta.setOnClickListener {
            val db = FirebaseFirestore.getInstance().collection("users").document(userId!!)
            val publicationRef = db.collection("publications").document(publication.id)
            //Si la coleccion likes no extiste, se crea
            publicationRef.collection("likes").document(userId).get().addOnSuccessListener {
                if (it.exists()) {
                    publicationRef.collection("likes").document(userId).delete()
                    holder.binding.btMeGusta.setImageResource(R.drawable.like_icon)
                } else {
                    publicationRef.collection("likes").document(userId).set(hashMapOf("liked" to true))
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