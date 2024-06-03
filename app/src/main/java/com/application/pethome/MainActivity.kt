package com.application.pethome

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.application.pethome.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

private var _binding: ActivityMainBinding? = null
private val binding
    get() = _binding!!

class MainActivity : AppCompatActivity() {

    companion object {
        const val CHANNEL_ID = "PetHome"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        // Comprobar si el usuario está logueado
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Redirigir al usuario a la pantalla principal
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_loginFragment_to_mainFragment)
        }

        // Crear canal de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, "PetHome", NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        getNotis()
        solicitarPermisos()

        setContentView(view)
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
                if (notis.size > 0) {
                    simpleNotification()
                }
            }
    }

    fun simpleNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentText("Tienes una nueva notificación")
            .setContentTitle("PetHome")
            .setSmallIcon(R.drawable.alert_icon)
            .build()
        notificationManager.notify(1, notification)
    }

    fun solicitarPermisos() {
        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permiso concedido
            } else {
                // Permiso denegado
            }
        }
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permiso concedido
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> {
                // Mostrar diálogo explicativo
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
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