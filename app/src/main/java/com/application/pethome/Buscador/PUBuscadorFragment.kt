package com.application.pethome.Buscador

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.application.pethome.User
import com.application.pethome.databinding.FragmentPUBuscadorBinding
import com.bumptech.glide.Glide

class PUBuscadorFragment : Fragment() {
    private var _binding: FragmentPUBuscadorBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPUBuscadorBinding.inflate(inflater, container, false)

        return binding.root
    }
}