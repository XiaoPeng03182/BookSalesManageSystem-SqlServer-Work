package com.example.booksalesmanagement

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.booksalesmanagement.databinding.ActivityBottomNavigationViewBinding

class BottomNavigationViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNavigationViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBottomNavigationViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //初始化
        initBottomNavigationViewActivity()

    }

    private fun initBottomNavigationViewActivity() {
        val navView: BottomNavigationView = binding.navView

        val navController =
            findNavController(R.id.nav_host_fragment_activity_bottom_navigation_view)

        navView.setupWithNavController(navController)
    }
}