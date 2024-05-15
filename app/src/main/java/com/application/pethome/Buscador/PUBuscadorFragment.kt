package com.application.pethome.Buscador

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.pethome.Chat
import com.application.pethome.Mascota
import com.application.pethome.MascotaAdapter
import com.application.pethome.Mensajeria.ChatAdapter
import com.application.pethome.Mesage
import com.application.pethome.R
import com.application.pethome.User
import com.application.pethome.databinding.FragmentPUBuscadorBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class PUBuscadorFragment : Fragment() {
    private var _binding: FragmentPUBuscadorBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    private lateinit var mascotaAdapter: MascotaAdapter

    private var chatAdapter: ChatAdapter? = null

    private lateinit var db: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPUBuscadorBinding.inflate(inflater, container, false)

        // Initialize the RecyclerView and its adapter
        mascotaAdapter = MascotaAdapter(listOf())
        binding.rvMascotasUsuarioBUSC.adapter = mascotaAdapter

        // Set a LinearLayoutManager to the RecyclerView
        binding.rvMascotasUsuarioBUSC.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Get the user from the arguments
        val user: User? = arguments?.getParcelable("user")

        // Use the user data to fill in the fields
        if (user != null) {
            // Replace these with the actual fields in your layout
            binding.tvNombreUsuarioBUSC.text = user.nombre
            binding.tvDescripcionBUSC.text = user.descripcion
            Glide.with(this)
                .load(user.imagen)
                .into(binding.imageView2BUSC)
            binding.txtUsuarioBUSC.text = user.nombre
            getMascotas(user.uid)
            getNumberOfPosts(user.uid)
            getNumberOfFollowed(user.uid)
            getNumberOfFollowers(user.uid)

            binding.btChatear.setOnClickListener {
                buscarChatExistente(
                    user
                )
            }
        }
    }

    private fun getNumberOfPosts(userId: String) {
        if (userId.isNotEmpty()) {
            val db = FirebaseFirestore.getInstance()
            val query = db.collection("users").document(userId)
                .collection("publications")

            query.count().get(AggregateSource.SERVER).addOnSuccessListener { task ->
                binding.tvPublicacionesCuentaBUSC.text = "${task.count}"
            }
        } else {

        }
    }

    private fun getNumberOfFollowed(userId: String) {
        if (userId.isNotEmpty()) {
            val db = FirebaseFirestore.getInstance()
            val query = db.collection("users").document(userId)
                .collection("seguidos")

            query.count().get(AggregateSource.SERVER).addOnSuccessListener { task ->
                binding.tvSeguidosCuentaBUSC.text = "${task.count}"
            }
        } else {

        }
    }

    private fun getNumberOfFollowers(userId: String) {
        if (userId.isNotEmpty()) {
            val db = FirebaseFirestore.getInstance()
            val query = db.collection("users").document(userId)
                .collection("seguidores")

            query.count().get(AggregateSource.SERVER).addOnSuccessListener { task ->
                binding.tvSeguidoresCuentaBUSC.text = "${task.count}"
            }
        } else {

        }
    }

    private fun getMascotas(userId: String) {
        if (userId.isNotEmpty()) {
            FirebaseFirestore.getInstance().collection("users").document(userId)
                .collection("mascotas")
                .get()
                .addOnSuccessListener { documents ->
                    val mascotas = documents.mapNotNull { it.toObject(Mascota::class.java) }
                    mascotaAdapter.updateMascotas(mascotas)
                }
        } else {
            // Handle the case where userId is null or empty
        }
    }

    private fun buscarChatExistente(user: User) {
        val uidUsuario1 = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val uidUsuario2 = user.uid
        val db = FirebaseFirestore.getInstance()

        val query = db.collection("chats")
            .whereIn("idUser1", listOf(uidUsuario1, uidUsuario2))
            .whereIn("idUser2", listOf(uidUsuario1, uidUsuario2))

        query.get().addOnSuccessListener { documents ->
            if (documents.isEmpty) {
                // No existe un chat entre estos dos usuarios, crear uno nuevo
                val chatId = crearNuevoChat(uidUsuario1, uidUsuario2)
                navegarAlChat(chatId, user)
            } else {
                // Existe un chat entre estos dos usuarios
                val chat = documents.documents[0].toObject(Chat::class.java)
                chat?.id?.let { chatId ->
                    recogerMensajes(chatId)
                    navegarAlChat(chatId, user)
                }
            }
        }
    }

    private fun navegarAlChat(chatId: String, user: User) {
        val bundle = Bundle().apply {
            putString("chatId", chatId)
            putString("userName", user.nombre)
            putString("userImage", user.imagen)
        }
        findNavController().navigate(
            R.id.action_PUBuscadorFragment_to_chatFragment,
            bundle
        )
    }

    private fun crearNuevoChat(uidUsuario1: String, uidUsuario2: String): String {
        val db = FirebaseFirestore.getInstance()
        val chat =
            Chat(id = UUID.randomUUID().toString(), idUser1 = uidUsuario1, idUser2 = uidUsuario2)
        chat.id?.let {
            db.collection("chats").document(it).set(chat)
                .addOnSuccessListener {
                    // Chat creado con éxito, puedes empezar a añadir mensajes
                }
        }
        return chat.id ?: ""
    }

    private fun recogerMensajes(idChat: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("chats").document(idChat).collection("mensajes")
            .orderBy("timestamp")
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