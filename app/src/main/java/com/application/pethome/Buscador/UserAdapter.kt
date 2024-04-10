package com.application.pethome.Buscador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.pethome.R
import com.application.pethome.User

class UserAdapter(private var users: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUsuario: TextView = view.findViewById(R.id.tvUsuario)
        val ivFotoPerfil: ImageView = view.findViewById(R.id.ivFotoPerfil)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.finder_users, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.tvUsuario.text = user.nombre
        // Aquí puedes usar una biblioteca como Glide o Picasso para cargar la imagen desde la URL
    }

    override fun getItemCount() = users.size

    fun filterList(filteredList: List<User>) {
        users = filteredList
        notifyDataSetChanged()
    }
}