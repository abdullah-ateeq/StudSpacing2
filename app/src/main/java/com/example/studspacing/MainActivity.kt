package com.example.studspacing

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.studspacing.databinding.ActivityMainBinding
import com.example.studspacing.ui.viewmodel.SettingsViewModel

/**
 * Single Activity container coordinating Jetpack Navigation and global application chrome.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply saved visual theme (Dark Mode / Light Mode)
        settingsViewModel.applySavedTheme()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)

        // Retrieve NavHostFragment and NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Top-level destinations (no back arrow displayed for these)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.resultFragment,
                R.id.settingsFragment,
                R.id.aboutFragment
            )
        )

        binding.topAppBar.setupWithNavController(navController, appBarConfiguration)
        binding.bottomNavigation.setupWithNavController(navController)

        // Dynamic visibility management for splash screen vs main screens
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.splashFragment) {
                binding.topAppBar.visibility = View.GONE
                binding.bottomNavigation.visibility = View.GONE
            } else {
                binding.topAppBar.visibility = View.VISIBLE
                binding.bottomNavigation.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null) || super.onSupportNavigateUp()
    }
}
