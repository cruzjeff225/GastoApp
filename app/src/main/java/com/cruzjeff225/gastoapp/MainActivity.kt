package com.cruzjeff225.gastoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cruzjeff225.gastoapp.databinding.ActivityMainBinding
import com.cruzjeff225.gastoapp.ui.home.HomeFragment
import com.cruzjeff225.gastoapp.ui.savingsgoal.SavingsGoalsFragment
import com.cruzjeff225.gastoapp.ui.profile.ProfileFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load HomeFragment by default
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.navigation_goals -> {
                    loadFragment(SavingsGoalsFragment())
                    true
                }
                R.id.navigation_profile -> {
                    // Create ProfileFragment
                    loadFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        // Avoid reloading the same fragment
        if (currentFragment?.javaClass == fragment.javaClass) {
            return
        }

        currentFragment = fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }

    // Public method to refresh HomeFragment from outside
    fun refreshHomeFragment() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (fragment is HomeFragment) {
            fragment.refreshData()
        }
    }
}