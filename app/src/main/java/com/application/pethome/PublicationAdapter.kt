package com.application.pethome

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.pethome.databinding.PublicationModelBinding
import com.squareup.picasso.Picasso

class PublicationAdapter(private var publications: List<Publication>) : RecyclerView.Adapter<PublicationAdapter.PublicationViewHolder>() {

    inner class PublicationViewHolder(val binding: PublicationModelBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicationViewHolder {
        val binding = PublicationModelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PublicationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PublicationViewHolder, position: Int) {
        val publication = publications[position]
        holder.binding.tvUsuario.text = publication.nombre
        Picasso.get().load(publication.imagen).into(holder.binding.ivPost)
    }

    override fun getItemCount() = publications.size

    @SuppressLint("NotifyDataSetChanged")
    fun updatePublications(newPublications: List<Publication>) {
        this.publications = newPublications
        notifyDataSetChanged()
    }
}