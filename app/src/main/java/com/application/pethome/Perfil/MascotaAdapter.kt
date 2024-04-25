package com.application.pethome

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.pethome.databinding.FragmentMascotaRowBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class MascotaAdapter(private var mascotas: List<Mascota>) :
    RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder>() {

    inner class MascotaViewHolder(val binding: FragmentMascotaRowBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaViewHolder {
        val binding =
            FragmentMascotaRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MascotaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MascotaViewHolder, position: Int) {
        val mascota = mascotas[position]
        holder.binding.tvNombreMascota.text = mascota.nombre
        Glide.with(holder.binding.imgMascota.context)
            .load(mascota.imagen)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(holder.binding.imgMascota)
        holder.binding.imgMascota.background = null
    }

    override fun getItemCount() = mascotas.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateMascotas(newMascotas: List<Mascota>) {
        this.mascotas = newMascotas
        notifyDataSetChanged()
    }
}