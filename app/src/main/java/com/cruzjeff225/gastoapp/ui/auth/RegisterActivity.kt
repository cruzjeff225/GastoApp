package com.cruzjeff225.gastoapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cruzjeff225.gastoapp.R
import com.cruzjeff225.gastoapp.data.repository.AuthRepository
import com.cruzjeff225.gastoapp.MainActivity
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    // Views
    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvSignInLink: TextView

    // Repository
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        initViews()

        // Setup listeners
        setupListeners()
    }

    private fun initViews() {
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvSignInLink = findViewById(R.id.tvSignInLink)
    }

    private fun setupListeners() {
        // Sign Up Button
        btnSignUp.setOnClickListener {
            handleSignUp()
        }

        // Sign In Link
        tvSignInLink.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun handleSignUp() {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // Validations
        if (!validateInputs(fullName, email, password, confirmPassword)) {
            return
        }

        setLoadingState(true)

        lifecycleScope.launch {
            val result = authRepository.registerUser(email, password, fullName)

            result.onSuccess { user ->
                Toast.makeText(
                    this@RegisterActivity,
                    "Bienvenido ${user.displayName}! Cuenta creada exitosamente",
                    Toast.LENGTH_SHORT
                ).show()
                navigateToMain()
            }.onFailure { exception ->
                setLoadingState(false)
                handleAuthError(exception)
            }
        }
    }

    private fun validateInputs(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        // Validate full name
        if (fullName.isEmpty()) {
            etFullName.error = getString(R.string.error_empty_field)
            etFullName.requestFocus()
            return false
        }

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

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = getString(R.string.error_empty_field)
            etConfirmPassword.requestFocus()
            return false
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = getString(R.string.error_passwords_not_match)
            etConfirmPassword.requestFocus()
            return false
        }

        return true
    }

    private fun setLoadingState(isLoading: Boolean) {
        btnSignUp.isEnabled = !isLoading
        etFullName.isEnabled = !isLoading
        etEmail.isEnabled = !isLoading
        etPassword.isEnabled = !isLoading
        etConfirmPassword.isEnabled = !isLoading
        tvSignInLink.isEnabled = !isLoading

        btnSignUp.text = if (isLoading) "Creando cuenta..." else getString(R.string.sign_up_button)
    }

    private fun handleAuthError(exception: Throwable) {
        val errorMessage = when (exception) {
            is FirebaseAuthUserCollisionException ->
                "El correo electrónico ya está en uso."
            is FirebaseAuthWeakPasswordException ->
                "La contraseña es demasiado débil."
            is FirebaseNetworkException ->
                "Error de red. Por favor, compruebe su conexión a internet."
            else -> "Error de autenticación: ${exception.message}"
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        finish() // Return to login screen
    }
}