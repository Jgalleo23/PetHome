package com.application.pethome.Mensajeria

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.application.pethome.R
import com.application.pethome.databinding.FragmentChatBinding
import com.application.pethome.databinding.FragmentMainBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.pethome.Mascota
import com.application.pethome.Mesage
import com.application.pethome.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import java.util.UUID

class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    private var chatAdapter: ChatAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        val userName = arguments?.getString("userName")
        val userImage = arguments?.getString("userImage")

        binding.tvNombreChat.text = userName
        Glide.with(this).load(userImage).into(binding.ivFotoChat)

        val chatId = arguments?.getString("chatId")!!
        chatAdapter = ChatAdapter(listOf(), chatId)

        binding.rvMensajesChat.adapter = chatAdapter
        binding.rvMensajesChat.layoutManager = LinearLayoutManager(context)

        recogerMensajes(chatId)
        updateMessages(FirebaseFirestore.getInstance(), chatId)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = FirebaseFirestore.getInstance()
        val chatId = arguments?.getString("chatId")!!
        var userName = ""
        var userImage = ""

        FirebaseAuth.getInstance().currentUser?.uid?.let {
            FirebaseFirestore.getInstance().collection("users").document(it).get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        userName = it.getString("nombre").toString()
                        userImage = it.getString("imagen").toString()
                    }
                }
        }

        binding.btEnviar.setOnClickListener {
            val mensajeTexto = binding.etMensaje.text.toString()

            if (mensajeTexto.isNotEmpty() && chatId != null && userName != null) {
                // Asegúrate de que 'userName' y 'userImage' son los datos del remitente
                val mensaje = Mesage(
                    id = UUID.randomUUID().toString(),
                    userName = userName,
                    userPhoto = userImage,
                    message = mensajeTexto,
                    timestamp = System.currentTimeMillis()
                )

                db.collection("chats").document(chatId).collection("mensajes")
                    .add(mensaje)
                    .addOnSuccessListener {
                        binding.etMensaje.text.clear()
                        updateMessages(db, chatId)
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
            }
        }
    }

    private fun updateMessages(db: FirebaseFirestore, chatId: String) {
        db.collection("chats").document(chatId).collection("mensajes")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val messages = result.documents.map { doc -> doc.toObject(Mesage::class.java)!! }
                chatAdapter?.updateMessages(messages)
            }
    }

    private fun recogerMensajes(idChat: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("chats").document(idChat).collection("mensajes")
            .orderBy("timestamp") // Ordena los mensajes por el timestamp
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val messages =
                        snapshot.documents.mapNotNull { it.toObject(Mesage::class.java) }
                    chatAdapter?.updateMessages(messages)
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
    }
}