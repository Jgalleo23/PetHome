package com.application.pethome.Publicaciones

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.application.pethome.R
import com.application.pethome.databinding.FragmentPublicacionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.UUID

class PublicacionFragment : Fragment() {
    private var _binding: FragmentPublicacionBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    private val PICK_IMAGE_REQUEST = 71
    private val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPublicacionBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()

        binding.ivFoto.setOnClickListener {
            showImagePickDialog()
        }

        binding.btSubir.setOnClickListener {
            if (binding.ivFoto.drawable == null) {
                Toast.makeText(context, "Por favor, seleccione una foto", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else {
                // Desactivar los campos
                binding.ivFoto.isEnabled = false
                binding.etDescripcion.isEnabled = false
                binding.btSubir.isEnabled = false

                val user = FirebaseAuth.getInstance().currentUser?.uid
                var nombre = ""
                var descripcion = binding.etDescripcion.text.toString()
                FirebaseAuth.getInstance().currentUser?.uid?.let {
                    FirebaseFirestore.getInstance().collection("users").document(it).get()
                        .addOnSuccessListener {
                            if (it.exists()) {
                                nombre = it.getString("nombre").toString()
                            }
                        }
                }
                // Generar un UUID único para la imagen
                val uniqueID = UUID.randomUUID().toString()

                // Subir la imagen a Firebase Storage y obtener la URL
                val storageRef =
                    FirebaseStorage.getInstance().reference.child("publications_images/$user/$uniqueID.jpg")
                val bitmap = (binding.ivFoto.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                // Mostrar el ProgressBar
                binding.progressBar.visibility = View.VISIBLE

                val uploadTask = storageRef.putBytes(data)
                uploadTask.addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()

                        val publicationData = hashMapOf(
                            "id" to uniqueID,
                            "nombre" to nombre,
                            "imagen" to imageUrl,
                            "descripcion" to descripcion
                        )

                        // Guardar la publicación en la subcolección del usuario
                        if (user != null) {
                            db.collection("users").document(user).collection("publications")
                                .add(publicationData)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        Toast.makeText(
                                            context,
                                            "Publicación subida correctamente",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        findNavController().navigate(R.id.action_publicacionFragment_to_mainFragment)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Ha ocurrido un error",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    // Ocultar el ProgressBar y reactivar los campos
                                    binding.progressBar.visibility = View.GONE
                                    binding.ivFoto.isEnabled = true
                                    binding.etDescripcion.isEnabled = true
                                    binding.btSubir.isEnabled = true
                                }
                        }
                    }
                }.addOnFailureListener {
                    // En caso de error, ocultar el ProgressBar y reactivar los campos
                    binding.progressBar.visibility = View.GONE
                    binding.ivFoto.isEnabled = true
                    binding.etDescripcion.isEnabled = true
                    binding.btSubir.isEnabled = true
                    Toast.makeText(context, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
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

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    // Esta función maneja el resultado de la selección de la imagen
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.ivFoto.setImageBitmap(imageBitmap)
            binding.ivFoto.background = null
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val uri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)
                binding.ivFoto.setImageBitmap(bitmap)
                binding.ivFoto.background = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showImagePickDialog() {
        val options = arrayOf("Tomar foto", "Seleccionar de la galería")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Elige una opción")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openCamera() // Si elige "Tomar foto", llama a openCamera()
                1 -> openImageChooser() // Si elige "Seleccionar de la galería", llama a openImageChooser()
            }
        }
        builder.show()
    }
}