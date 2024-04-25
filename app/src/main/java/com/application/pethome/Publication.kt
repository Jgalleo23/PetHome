package com.application.pethome

data class Publication(
    val id: String = "",
    val nombre: String = "",
    val imagen: String = "",
    val likes: MutableList<String> = mutableListOf()
)