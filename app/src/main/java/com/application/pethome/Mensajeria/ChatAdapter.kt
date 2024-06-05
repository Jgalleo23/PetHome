package com.application.pethome.Mensajeria

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.pethome.R
import com.application.pethome.Objetos.User
import com.application.pethome.Objetos.Mesage
import com.squareup.picasso.Picasso

class ChatAdapter(private var messages: List<Mesage>, private val chatId: String) :
    RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombreUsuario: TextView = view.findViewById(R.id.tvNombreUsuarioMensaje)
        val tvMensaje: TextView = view.findViewById(R.id.tvMensaje)
        val civFotoMensaje: ImageView =
            view.findViewById(R.id.civFotoMensaje) // Asegúrate de tener este ImageView en tu layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_mensajes, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]

        // Utiliza los datos del usuario que están almacenados en el objeto Message
        val user = User(
            nombre = message.userName, imagen = message.userPhoto
        )
        if (user != null) {
            holder.tvNombreUsuario.text = user.nombre
            holder.tvMensaje.text = message.message
            if (user.imagen.isNotEmpty()) {
                Picasso.get().load(user.imagen).into(holder.civFotoMensaje)
            } else {
                // Load a default image or do nothing
            }
        }
    }

    override fun getItemCount() = messages.size

    fun updateMessages(messages: List<Mesage>) {
        this.messages = messages
        notifyDataSetChanged()
    }
}