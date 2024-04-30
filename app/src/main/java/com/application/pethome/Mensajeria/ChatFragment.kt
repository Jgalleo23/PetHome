package com.application.pethome.Mensajeria

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.application.pethome.R
import com.application.pethome.databinding.FragmentChatBinding
import com.application.pethome.databinding.FragmentMainBinding

class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        return binding.root
    }

}