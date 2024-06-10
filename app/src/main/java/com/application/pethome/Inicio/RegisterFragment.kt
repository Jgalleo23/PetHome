package com.application.pethome.Inicio

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap.CompressFormat
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.application.pethome.R
import com.application.pethome.databinding.FragmentLoginBinding
import com.application.pethome.databinding.FragmentRegisterBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")
    private lateinit var spinnerSexo: Spinner

    private lateinit var db: FirebaseFirestore

    private val PICK_IMAGE_REQUEST = 71


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        spinnerSexo = binding.spinnerSexo
        val adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.sexos, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSexo.adapter = adapter

        binding.ivPerfil.setOnClickListener {
            openImageChooser()
        }

        binding.chkMostrar.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.etContrasena.transformationMethod = null // Mostrar contraseña
            } else {
                binding.etContrasena.transformationMethod =
                    PasswordTransformationMethod.getInstance() // Ocultar contraseña
            }
        }

        binding.etCorreo.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && !android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etCorreo.text)
                    .matches()
            ) {
                binding.etCorreo.error = "Correo inválido"
            }
        }

        binding.etContrasena.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && binding.etContrasena.text.length < 6) {
                binding.etContrasena.error = "La contraseña debe tener al menos 6 caracteres"
            }
        }

        binding.btnRegistrar.setOnClickListener {
            // Mostrar el ProgressBar
            binding.progressBar.visibility = View.VISIBLE
            binding.btnRegistrar.isEnabled = false
            binding.txtISesion.isEnabled = false

            if (binding.etCorreo.text.toString().isEmpty() ||
                binding.etContrasena.text.isEmpty() ||
                binding.etDescripcion.text.toString().isEmpty() ||
                binding.etUsuario.text.toString().isEmpty() ||
                spinnerSexo.selectedItem == null
            ) {
                Toast.makeText(context, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                binding.btnRegistrar.isEnabled = true
                binding.txtISesion.isEnabled = true
                return@setOnClickListener
            } else if (binding.ivPerfil.drawable == null) {
                Toast.makeText(context, "Por favor, seleccione una foto de perfil", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                binding.btnRegistrar.isEnabled = true
                binding.txtISesion.isEnabled = true
                return@setOnClickListener
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.etCorreo.text.toString(), binding.etContrasena.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = FirebaseAuth.getInstance().currentUser
                        user?.sendEmailVerification()?.addOnCompleteListener { verTask ->
                            if (verTask.isSuccessful) {
                                FirebaseMessaging.getInstance().token.addOnCompleteListener(
                                    OnCompleteListener { tokenTask ->
                                        if (!tokenTask.isSuccessful) {
                                            Log.w(
                                                TAG,
                                                "Fetching FCM registration token failed",
                                                tokenTask.exception
                                            )
                                            binding.btnRegistrar.isEnabled = true
                                            binding.progressBar.visibility = View.GONE
                                            binding.txtISesion.isEnabled = true
                                            return@OnCompleteListener
                                        }
                                        val correo = binding.etCorreo.text.toString().trim()
                                        val nombre = binding.etUsuario.text.toString().trim()
                                        val sexo = spinnerSexo.selectedItem.toString().trim()
                                        val descripcion =
                                            binding.etDescripcion.text.toString().trim()
                                        val token = tokenTask.result

                                        // Subir la imagen a Firebase Storage y obtener la URL
                                        val storageRef =
                                            FirebaseStorage.getInstance().reference.child("profile_images")
                                                .child("${user.uid}.jpg")
                                        val bitmap =
                                            (binding.ivPerfil.drawable as BitmapDrawable).bitmap
                                        val baos = ByteArrayOutputStream()
                                        bitmap.compress(CompressFormat.JPEG, 100, baos)
                                        val data = baos.toByteArray()

                                        val uploadTask = storageRef.putBytes(data)
                                        uploadTask.addOnSuccessListener {
                                            storageRef.downloadUrl.addOnSuccessListener { uri ->
                                                val imageUrl = uri.toString()

                                                val userData = hashMapOf(
                                                    "uid" to user.uid,
                                                    "nombre" to nombre,
                                                    "correo" to correo,
                                                    "sexo" to sexo,
                                                    "descripcion" to descripcion,
                                                    "imagen" to imageUrl,
                                                    "token" to token
                                                )

                                                db.collection("users").document(user.uid)
                                                    .set(userData)
                                                    .addOnCompleteListener {
                                                        if (it.isSuccessful) {
                                                            Toast.makeText(
                                                                context,
                                                                "Usuario registrado correctamente, por favor verifica tu correo",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            binding.progressBar?.visibility
                                                                ?: View.GONE
                                                            findNavController().navigate(R.id.action_registerFragment_to_authFragment)
                                                        } else {
                                                            binding.etCorreo.error =
                                                                "Ha ocurrido un error"
                                                        }
                                                    }
                                            }
                                        }.addOnFailureListener {
                                            // Manejar el error
                                        }
                                    })
                            } else {
                                Toast.makeText(
                                    context,
                                    "Error al enviar correo de verificación",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.btnRegistrar.isEnabled = true
                                binding.progressBar.visibility = View.GONE
                                binding.txtISesion.isEnabled = true
                            }
                        }
                    } else {
                        binding.etCorreo.error = "Ha ocurrido un error"
                        binding.btnRegistrar.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                        binding.txtISesion.isEnabled = true
                    }
                }
            }
        }

        binding.txtISesion.paintFlags = binding.txtISesion.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.txtISesion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
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
                binding.ivPerfil.setImageBitmap(bitmap)
                binding.ivPerfil.background = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}