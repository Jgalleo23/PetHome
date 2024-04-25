package com.application.pethome.Perfil

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.application.pethome.databinding.FragmentRegMascotaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class RegMascotaFragment : Fragment() {
    private var _binding: FragmentRegMascotaBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val PICK_IMAGE_REQUEST = 71
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegMascotaBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.ivMascota.setOnClickListener {
            openImageChooser()
        }


        binding.btRegistrarMascota.setOnClickListener {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val nombreMascota = binding.etNombreMascota.text.toString()
                val edad = binding.etUsuario.text.toString().toInt()
                val descripcion = binding.etDescripcion.text.toString()
                val raza = binding.spinnerRaza.selectedItem.toString()
                val id = db.collection("users").document(userId).collection("mascotas").document().id

                // Subir la imagen a Firebase Storage y obtener la URL
                val storageRef =
                    FirebaseStorage.getInstance().reference.child("mascotas_images")
                        .child("$id.jpg")
                val bitmap = (binding.ivMascota.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                // Check if the fields are not empty
                if (nombreMascota.isNotEmpty() && descripcion.isNotEmpty()) {
                    val uploadTask = storageRef.putBytes(data)
                    uploadTask.addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()

                            val mascota = hashMapOf(
                                "imagen" to imageUrl,
                                "nombre" to nombreMascota,
                                "raza" to raza,
                                "edad" to edad,
                                "descripcion" to descripcion,
                                "id" to id,
                                "idUsuario" to userId
                            )

                            // Add a new document to the "mascotas" collection of the current user
                            db.collection("users").document(userId).collection("mascotas")
                                .add(mascota)
                                .addOnSuccessListener { documentReference ->
                                    Log.d(
                                        TAG,
                                        "DocumentSnapshot added with ID: ${documentReference.id}"
                                    )
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error adding document", e)
                                }
                        }
                    }
                } else {

                    Toast.makeText(
                        context,
                        "Por favor, rellene todos los campos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        return binding.root
    }

    private fun openImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    // Esta función maneja el resultado de la selección de la imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val uri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)
                binding.ivMascota.setImageBitmap(bitmap)
                binding.ivMascota.background = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}