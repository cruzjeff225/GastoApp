package com.cruzjeff225.gastoapp.data.model

import java.util.Date

data class User(
    var id: String = "",
    val fullName: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val phoneNumber: String = "",
    val createdAt: Long = Date().time,
    val updatedAt: Long = Date().time,

    // Preferencias de usuario
    val currency: String = "USD",
    val language: String = "es",
    val notificationsEnabled: Boolean = true,
    val biometricEnabled: Boolean = false
)