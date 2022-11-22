package com.example.androidtechnote.recycler.custom_focus

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityCustomFocusBinding


class CustomFocusActivity : AppCompatActivity() {

    lateinit var binding : ActivityCustomFocusBinding
    lateinit var navHostFragment : NavHostFragment

    private val viewModel : CustomFocusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_custom_focus)

        navHostFragment = supportFragmentManager.findFragmentById(binding.navHost.id) as NavHostFragment
    }
}
