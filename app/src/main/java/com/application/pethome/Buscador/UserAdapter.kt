package com.application.pethome.Buscador

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.pethome.R
import com.application.pethome.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserAdapter(private var users: List<User>, private val userSelected: (User) -> Unit) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUsuario: TextView = view.findViewById(R.id.tvUsuario)
        val btSeguir: Button = view.findViewById(R.id.btSeguir)

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

        // Get the current user's ID
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        // Create a reference to the Firestore database
        val db = FirebaseFirestore.getInstance()

        /**if (currentUserId != null) {
        // Check if the user is already followed
        db.collection("users").document(currentUserId).collection("seguidos")
        .get()
        .addOnSuccessListener { result ->
        for (document in result) {
        if (document.data["uid"] == user.uid) {
        holder.btSeguir.isEnabled = false
        }
        }
        }
        }**/

        if (currentUserId != null) {
            // Check if the user is already followed
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
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
            }

            userSelected(user)
        }
    }

    override fun getItemCount() = users.size

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filteredList: List<User>) {
        users = filteredList
        notifyDataSetChanged()
    }
}