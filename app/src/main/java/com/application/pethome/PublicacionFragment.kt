package com.application.pethome

import android.app.Activity
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

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPublicacionBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()

        binding.ivFoto.setOnClickListener {
            openImageChooser()
        }

        binding.btSubir.setOnClickListener {
            if (binding.ivFoto.drawable == null) {
                Toast.makeText(context, "Por favor, seleccione una foto", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else {
                val user = FirebaseAuth.getInstance().currentUser?.uid
                var nombre = ""
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
                            "nombre" to nombre,
                            "imagen" to imageUrl
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
                                    // Ocultar el ProgressBar
                                    binding.progressBar.visibility = View.GONE
                                }
                        }
                    }
                }
            }
        }
        return binding.root
    }

    private fun openImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            PICK_IMAGE_REQUEST
        )
    }

    // Esta función maneja el resultado de la selección de la imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
}