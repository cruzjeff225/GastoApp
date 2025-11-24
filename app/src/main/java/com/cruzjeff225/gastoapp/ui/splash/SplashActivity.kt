package com.cruzjeff225.gastoapp.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.cruzjeff225.gastoapp.R
import com.cruzjeff225.gastoapp.data.repository.AuthRepository
import com.cruzjeff225.gastoapp.ui.auth.LoginActivity
import com.cruzjeff225.gastoapp.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val authRepository = AuthRepository()
    private val splashDuration = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Show the splash screen for a few seconds
        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthenticationAndNavigate()
        }, splashDuration)
    }

    private fun checkAuthenticationAndNavigate() {
        if (authRepository.isUserLoggedIn()) {
            // User logged in, navigate to MainActivity
            navigateToMain()
        } else {
            // User not logged in, navigate to LoginActivity
            navigateToLogin()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}