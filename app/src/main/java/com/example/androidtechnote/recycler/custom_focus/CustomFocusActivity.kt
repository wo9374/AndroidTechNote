package com.example.androidtechnote.recycler.custom_focus

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityCustomFocusBinding
import com.example.customlibrary.FocusItem
import com.example.customlibrary.MoviesRepository
import kotlinx.coroutines.flow.collectLatest


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
