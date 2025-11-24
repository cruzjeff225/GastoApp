package com.cruzjeff225.gastoapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cruzjeff225.gastoapp.R
import com.cruzjeff225.gastoapp.data.repository.AuthRepository
import com.cruzjeff225.gastoapp.MainActivity
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    // Views
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignIn: Button
    private lateinit var tvSignUpLink: TextView
    private lateinit var progressBar: ProgressBar

    // Repository
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Check if the user is already logged in
        if (authRepository.isUserLoggedIn()) {
            navigateToMain()
            return
        }

        // Initialize views
        initViews()

        // Setup listeners
        setupListeners()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnSignIn = findViewById(R.id.btnSignIn)
        tvSignUpLink = findViewById(R.id.tvSignUpLink)
    }

    private fun setupListeners() {
        // Sign In Button
        btnSignIn.setOnClickListener {
            handleSignIn()
        }

        // Sign Up Link
        tvSignUpLink.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun handleSignIn() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validations
        if (!validateInputs(email, password)) {
            return
        }

        setLoadingState(true)

        // Call the loginUser function from the AuthRepository
        lifecycleScope.launch {
            val result = authRepository.loginUser(email, password)

            result.onSuccess { user ->
                Toast.makeText(
                    this@LoginActivity,
                    "Bienvenido ${user.displayName ?: user.email}!",
                    Toast.LENGTH_SHORT
                ).show()
                navigateToMain()
            }.onFailure { exception ->
                setLoadingState(false)
                handleAuthError(exception)
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        // Validate email
        if (email.isEmpty()) {
            etEmail.error = getString(R.string.error_empty_field)
            etEmail.requestFocus()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = getString(R.string.error_invalid_email)
            etEmail.requestFocus()
            return false
        }

        // Validate password
        if (password.isEmpty()) {
            etPassword.error = getString(R.string.error_empty_field)
            etPassword.requestFocus()
            return false
        }

        if (password.length < 6) {
            etPassword.error = getString(R.string.error_password_short)
            etPassword.requestFocus()
            return false
        }

        return true
    }

    private fun setLoadingState(isLoading: Boolean) {
        btnSignIn.isEnabled = !isLoading
        etEmail.isEnabled = !isLoading
        etPassword.isEnabled = !isLoading
        tvSignUpLink.isEnabled = !isLoading

        btnSignIn.text = if (isLoading) "Iniciar Sesión..." else getString(R.string.sign_in_button)
    }

    private fun handleAuthError(exception: Throwable) {
        val errorMessage = when (exception) {
            is FirebaseAuthInvalidCredentialsException ->
                "Correo electrónico o contraseña incorrectos"
            is FirebaseNetworkException ->
                "Error de conexión. Verifica tu conexión a internet"
            else -> "Login failed: ${exception.message}"
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}