package com.application.pethome.Notificaciones

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.application.pethome.Objetos.Notification
import com.application.pethome.databinding.NotiModelBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class NotisAdapter(private var notis: List<Notification>) :
    RecyclerView.Adapter<NotisAdapter.NotiViewHolder>() {

    class NotiViewHolder(val binding: NotiModelBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotiViewHolder {
        val binding = NotiModelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotiViewHolder, position: Int) {
        val noti = notis[position]
        holder.binding.textNoti.text = "¿Quieres recibir a ${noti.mascotaNombre} como mascota?"

        holder.binding.btnAceptar.setOnClickListener {
            val userId = Firebase.auth.currentUser?.uid ?: ""
            val db = FirebaseFirestore.getInstance()

            // Crear un nuevo documento a partir del id de la mascota
            val mascotaDoc = db.collection("users").document(userId)
                .collection("mascotas").document()
            mascotaDoc.set(
                hashMapOf(
                    "nombre" to noti.mascotaNombre,
                    "raza" to noti.mascotaRaza,
                    "edad" to noti.mascotaEdad,
                    "descripcion" to noti.mascotaDescripcion,
                    "imagen" to noti.mascotaImagen,
                    "idUsuario" to noti.asignadoId,
                    "id" to mascotaDoc.id
                )
            )

            // Eliminar la mascota de la coleccion mascotas del asignador
            db.collection("users").document(noti.asignadorId)
                .collection("mascotas").document(noti.mascotaId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(
                        holder.itemView.context,
                        "Mascota aceptada",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            // Eliminar la notificación
            db.collection("users").document(userId)
                .collection("notis").document(noti.id)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(
                        holder.itemView.context,
                        "--",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        holder.binding.btnRechazar.setOnClickListener {
            // Eliminar la notificación
            val userId = Firebase.auth.currentUser?.uid ?: ""
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId)
                .collection("notis").document(noti.id)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(
                        holder.itemView.context,
                        "Notificación rechazada",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    override fun getItemCount() = notis.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateNotis(newNotis: List<Notification>) {
        notis = newNotis
        notifyDataSetChanged()
    }


}