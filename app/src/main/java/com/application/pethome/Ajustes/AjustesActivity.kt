package com.application.pethome.Ajustes

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.application.pethome.databinding.ActivityAjustesBinding

private var _binding: ActivityAjustesBinding? = null
private val binding
    get() = _binding!!

class AjustesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAjustesBinding.inflate(layoutInflater)
        val view = binding.root
        enableEdgeToEdge()

        setContentView(view)
    }
}