package com.application.pethome

import android.Manifest
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.pethome.databinding.FragmentMainBinding
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    private lateinit var publicationAdapter: PublicationAdapter

    companion object {
        const val CHANNEL_ID = "PetHome"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        publicationAdapter = PublicationAdapter(listOf())
        binding.rvMascotas.adapter = publicationAdapter
        binding.rvMascotas.layoutManager = LinearLayoutManager(context)

        getPublications()

        binding.btPerfil.setOnClickListener {
            // Redirigir a la pantalla de perfil
            findNavController().navigate(R.id.action_mainFragment_to_perfilFragment)
        }

        binding.btAjustes.setOnClickListener {
            // Redirigir a la pantalla de ajustes
            findNavController().navigate(R.id.action_mainFragment_to_ajustesFragment)
        }

        binding.btBuscar.setOnClickListener {
            // Redirigir a la pantalla de buscador
            findNavController().navigate(R.id.action_mainFragment_to_buscadorFragment)
        }

        binding.btAnadir.setOnClickListener() {
            // Redirigir a la pantalla de publicación
            findNavController().navigate(R.id.action_mainFragment_to_publicacionFragment)
        }

        binding.btNotis.setOnClickListener {
            // Redirigir a la pantalla de notificaciones
            findNavController().navigate(R.id.action_mainFragment_to_notisFragment)
        }

        getNotis()

        return binding.root
    }

    private fun getPublications() {
        val db = FirebaseFirestore.getInstance()

        db.collectionGroup("publications")
            .get()
            .addOnSuccessListener { documents ->
                val publications = mutableListOf<Publication>()
                for (document in documents) {
                    val publication = document.toObject(Publication::class.java)
                    publications.add(publication)
                }
                publicationAdapter.updatePublications(publications)
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    @OptIn(ExperimentalBadgeUtils::class)
    private fun updateNotificationBadge(numberOfNotifications: Int) {
        val badgeDrawable = BadgeDrawable.create(requireContext())
        badgeDrawable.number = numberOfNotifications
        badgeDrawable.backgroundColor = ContextCompat.getColor(requireContext(), R.color.red)
        badgeDrawable.badgeGravity = BadgeDrawable.TOP_END
        BadgeUtils.attachBadgeDrawable(badgeDrawable, binding.btNotis, null)
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
                updateNotificationBadge(notis.size)
                if (notis.size > 0) {
                    simpleNotification()
                }
            }
    }

    fun simpleNotification() {
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setContentText("Tienes una nueva notificación")
            .setContentTitle("PetHome")
            .setSmallIcon(R.drawable.alert_icon)
            .build()
        notificationManager.notify(1, notification)
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

}