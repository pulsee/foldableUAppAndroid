package com.example.demo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowMetricsCalculator
import com.example.demo.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       // val navView: NavigationRailView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,
                R.id.navigation_ecom_prodlist
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        //navView.setupWithNavController(navController)

        // Compute the window size class
        //updateNavigationForWindowSizeClass(computeWindowSizeClass().windowWidthSizeClass)
        // Use ViewTreeObserver to ensure the view is laid out before computing the size
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            updateNavigationForWindowSizeClass(computeWindowSizeClass().windowWidthSizeClass)
            binding.root.viewTreeObserver.removeOnGlobalLayoutListener { this }
        }

        // Observe window size changes [if this code is removed then selected tabs state will not retain on fold/unfold]
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                WindowInfoTracker.getOrCreate(this@MainActivity)
                    .windowLayoutInfo(this@MainActivity)
                    .collect { layoutInfo ->
                        val widthSizeClass = computeWindowSizeClass().windowWidthSizeClass
                        updateNavigationForWindowSizeClass(widthSizeClass)
                    }
            }
        }

    }


    private fun computeWindowSizeClass(): WindowSizeClass {
        val metrics = WindowMetricsCalculator.getOrCreate()
            .computeCurrentWindowMetrics(this)

        val widthDp = metrics.bounds.width() /
                resources.displayMetrics.density
        val heightDp = metrics.bounds.height() /
                resources.displayMetrics.density

        return WindowSizeClass.compute(widthDp, heightDp)
    }

    private fun updateNavigationForWindowSizeClass(widthSizeClass: WindowWidthSizeClass) {
        when (widthSizeClass) {
            WindowWidthSizeClass.COMPACT -> {
                binding.bottomNavView.setupWithNavController(findNavController(R.id.nav_host_fragment_activity_main))
                binding.bottomNavView.visibility = View.VISIBLE
                binding.navRail.visibility = View.GONE
            }
            WindowWidthSizeClass.MEDIUM, WindowWidthSizeClass.EXPANDED -> {
                binding.navRail.setupWithNavController(findNavController(R.id.nav_host_fragment_activity_main))
                binding.navRail.visibility = View.VISIBLE
                binding.bottomNavView.visibility = View.GONE
            }
            else -> {
                // Handle other cases if needed
            }
        }
    }
}