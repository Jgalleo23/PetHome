package com.application.pethome.Notificaciones

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.pethome.Objetos.Notification
import com.application.pethome.databinding.FragmentNotisBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class NotisFragment : Fragment() {
    //Implementa binding
    private var _binding: FragmentNotisBinding? = null
    private val binding get() = _binding!!

    private lateinit var notisAdapter: NotisAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNotisBinding.inflate(inflater, container, false)

        notisAdapter = NotisAdapter(listOf())
        binding.rvNotis.adapter = notisAdapter
        binding.rvNotis.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    private val handler = Handler()
    private val runnableCode = object : Runnable {
        override fun run() {
            getNotis()
            handler.postDelayed(this, 5000)
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(runnableCode)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnableCode)
    }

    private fun getNotis() {
        val db = FirebaseFirestore.getInstance()
        val userId = Firebase.auth.currentUser?.uid ?: ""
        db.collection("users").document(userId)
            .collection("notis")
            .get()
            .addOnSuccessListener { documents ->
                val notis = documents.map { doc ->
                    Notification(
                        title = doc.getString("title") ?: "",
                        message = doc.getString("message") ?: "",
                        asignadorId = doc.getString("asignadorId") ?: "",
                        asignadorNombre = doc.getString("asignadorNombre") ?: "",
                        mascotaImagen = doc.getString("mascotaImagen") ?: "",
                        mascotaNombre = doc.getString("mascotaNombre") ?: "",
                        mascotaRaza = doc.getString("mascotaRaza") ?: "",
                        mascotaEdad = doc.getLong("mascotaEdad")?.toInt() ?: 0,
                        mascotaDescripcion = doc.getString("mascotaDescripcion") ?: "",
                        mascotaId = doc.getString("mascotaId") ?: "",
                        id = doc.id,
                        asignadoId = doc.getString("asignadoId") ?: "",
                        asignadoNombre = doc.getString("asignadoNombre") ?: ""
                    )
                }
                notisAdapter.updateNotis(notis)
            }
    }

}