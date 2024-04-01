package com.example.amongus.model

data class User(
    val _id : String? = null,
    val username : String,
    val password: String,
    val connected : Boolean?,
    val role : String?,
    val messages: List<String>?
)

