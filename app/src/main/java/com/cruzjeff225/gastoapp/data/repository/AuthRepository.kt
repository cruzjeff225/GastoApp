package com.cruzjeff225.gastoapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Get current user
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // Check if user is logged in
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // New user registration
    suspend fun registerUser(email: String, password: String, fullName: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            // Update user profile with full name
            if (user != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName)
                    .build()
                user.updateProfile(profileUpdates).await()
            }

            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Registro fallido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Login user
    suspend fun loginUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Inicio de sesión fallido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Logout user
    fun logout() {
        auth.signOut()
    }
}