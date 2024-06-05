package com.application.pethome.Buscador

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.application.pethome.R
import com.application.pethome.Objetos.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class UserAdapter(private var users: List<User>, private val userSelected: (User) -> Unit) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

        // ViewHolder class para el RecyclerView
    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUsuario: TextView = view.findViewById(R.id.tvUsuario)
        val btSeguir: Button = view.findViewById(R.id.btSeguir)
        val ivFotoPerfil: ImageView = view.findViewById(R.id.ivFotoPerfil)

        init {
            view.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = users[position]
                    userSelected(user)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.finder_users, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.tvUsuario.text = user.nombre

        // Obtener el ID del usuario actual
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        // Referencia a la base de datos
        val db = FirebaseFirestore.getInstance()

        // Comprobar si el usuario actual ya sigue al usuario actual
        if (currentUserId != null) {
            db.collection("users").document(currentUserId).collection("seguidos")
                .whereEqualTo("uid", user.uid)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        // User is already followed, disable the button
                        holder.btSeguir.isEnabled = false
                        holder.btSeguir.setText("Seguido")
                    }
                }
        }

        // Navegar al perfil del usuario al hacer clic en la foto de perfil
        holder.ivFotoPerfil.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .whereEqualTo("nombre", user.nombre)
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
                                R.id.action_buscadorFragment_to_PUBuscadorFragment,
                                bundle
                            )
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error obteniendo documentos: ", exception)
                }
        }

        // Seguir al usuario al hacer clic en el botón
        holder.btSeguir.setOnClickListener {
            // Create a new document in the "seguidos" collection of the current user
            val followedUser = hashMapOf(
                "nombre" to user.nombre,
                "uid" to user.uid
            )

            if (currentUserId != null && holder.btSeguir.isEnabled) {
                db.collection("users").document(currentUserId).collection("seguidos")
                    .add(followedUser)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        holder.btSeguir.isEnabled = false

                        // Create the "seguidores" collection if it doesn't exist
                        db.collection("users").document(user.uid).collection("seguidores")
                            .document(currentUserId)
                            .set(hashMapOf<String, Any>())
                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot successfully written!")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error writing document", e)
                            }

                        // Increment the follower count of the followed user
                        db.collection("users").document(user.uid)
                            .update("seguidores", FieldValue.increment(1))
                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot successfully updated!")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error updating document", e)
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
            }

            userSelected(user)
        }
    }

    // Devuelve el número de elementos en la lista
    override fun getItemCount() = users.size

    // Actualiza la lista de usuarios
    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filteredList: List<User>) {
        users = filteredList
        notifyDataSetChanged()
    }
}