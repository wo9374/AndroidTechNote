package com.example.androidtechnote.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityNavigationBinding


class NavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityNavigationBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_navigation
        )
        setContentView(binding.root)

        NavigationUI.setupWithNavController(
            binding.mainBottomNavigation,
            findNavController(R.id.nav_host)
        )
    }
}