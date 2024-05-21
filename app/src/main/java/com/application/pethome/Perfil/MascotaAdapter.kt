package com.application.pethome

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.application.pethome.databinding.FragmentMascotaRowBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class MascotaAdapter(private var mascotas: List<Mascota>) :
    RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder>() {

    @SuppressLint("MissingInflatedId")
    inner class MascotaViewHolder(val binding: FragmentMascotaRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.imgMascota.setOnClickListener() {
                val context = it.context
                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_spinner, null)
                val spinner = dialogView.findViewById<Spinner>(R.id.spinnerCuadro)

                // Obtén los nombres de los usuarios seguidos
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                val db = FirebaseFirestore.getInstance()
                db.collection("users").document(userId!!).collection("seguidos")
                    .get()
                    .addOnSuccessListener { documents ->
                        val seguidos = documents.map { it.getString("nombre")!! }
                        // Llena el Spinner con los nombres de los usuarios seguidos
                        val adapter =
                            ArrayAdapter(context, android.R.layout.simple_spinner_item, seguidos)
                        spinner.adapter = adapter

                        // Muestra el cuadro de diálogo
                        AlertDialog.Builder(context)
                            .setTitle("Selecciona un usuario")
                            .setView(dialogView)
                            .setPositiveButton("Asignar mascota") { dialog, _ ->
                                // Haz algo con el usuario seleccionado
                                val selectedUserName = spinner.selectedItem.toString()

                                // Obtén la mascota seleccionada
                                val selectedMascota = mascotas[adapterPosition]

                                // Asigna la mascota al usuario seleccionado
                                asignarMascotaAUsuario(selectedUserName, selectedMascota, context)

                                // Elimina la mascota del usuario actual
                                eliminarMascotaDeUsuario(userId, selectedMascota, context)

                                dialog.dismiss()
                            }
                            .setNegativeButton("Cancelar") { dialog, _ ->
                                dialog.cancel()
                            }
                            .create()
                            .show()
                    }
                    .addOnFailureListener { exception ->
                        // Muestra un mensaje de error
                        Toast.makeText(
                            context,
                            "Error obteniendo usuarios seguidos: ${exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaViewHolder {
        val binding =
            FragmentMascotaRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MascotaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MascotaViewHolder, position: Int) {
        val mascota = mascotas[position]
        holder.binding.tvNombreMascota.text = mascota.nombre
        Glide.with(holder.binding.imgMascota.context)
            .load(mascota.imagen)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(holder.binding.imgMascota)
        holder.binding.imgMascota.background = null
    }

    override fun getItemCount() = mascotas.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateMascotas(newMascotas: List<Mascota>) {
        this.mascotas = newMascotas
        notifyDataSetChanged()
    }

    private fun asignarMascotaAUsuario(
        selectedUserName: String,
        selectedMascota: Mascota,
        context: Context
    ) {
        val db = FirebaseFirestore.getInstance()
        // Busca el documento del usuario seleccionado por su nombre de usuario
        db.collection("users").whereEqualTo("nombre", selectedUserName)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.documents.isNotEmpty()) {
                    val selectedUserDocument = documents.documents[0]

                    // Agrega la mascota a la colección de mascotas del usuario seleccionado
                    db.collection("users").document(selectedUserDocument.id)
                        .collection("mascotas")
                        .document(selectedMascota.id)
                        .set(selectedMascota.copy(id = selectedUserDocument.id)) // Store the DocumentReference's path as a String
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Mascota asignada con éxito",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                context,
                                "Error asignando mascota: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(
                        context,
                        "No se encontró el usuario seleccionado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    context,
                    "Error buscando el usuario seleccionado: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun eliminarMascotaDeUsuario(
        userId: String,
        selectedMascota: Mascota,
        context: Context
    ) {
        val db = FirebaseFirestore.getInstance()
        // Elimina la mascota de la colección de mascotas del usuario actual
        db.collection("users").document(userId)
            .collection("mascotas")
            .document(selectedMascota.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "Mascota eliminada con éxito",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    context,
                    "Error eliminando mascota: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}