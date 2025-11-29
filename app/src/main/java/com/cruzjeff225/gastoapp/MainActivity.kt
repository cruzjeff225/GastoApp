package com.cruzjeff225.gastoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cruzjeff225.gastoapp.ui.home.HomeFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Cargar HomeFragment al iniciar
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, HomeFragment())
                .commit()
        }
    }
}