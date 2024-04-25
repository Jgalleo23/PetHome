package com.application.pethome.Buscador

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.pethome.User
import com.application.pethome.databinding.FragmentBuscadorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BuscadorFragment : Fragment() {
    private var _binding: FragmentBuscadorBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    private lateinit var userAdapter: UserAdapter

    private var users: List<User> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuscadorBinding.inflate(inflater, container, false)
        userAdapter = UserAdapter(listOf()) { user ->

        }
        binding.rvUsuarios.adapter = userAdapter
        binding.rvUsuarios.layoutManager = LinearLayoutManager(context)

        getUsers()

        binding.svBuscador.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = users.filter { user: User ->
                    user.nombre.contains(newText ?: "", ignoreCase = true)
                }
                userAdapter.filterList(filteredList)
                return false
            }
        })

        return binding.root
    }

    private fun getUsers() {
        val db = FirebaseFirestore.getInstance()
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        // Limpiar la lista de usuarios antes de agregar nuevos usuarios
        users = listOf()

        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Exclude the current user from the list
                    if (document.id != currentUserId) {
                        val user = document.toObject(User::class.java)
                        users = users + user
                    }
                }
                userAdapter.filterList(users)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
}