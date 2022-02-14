package com.example.androidtechnote.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.androidtechnote.R

class FirstHomeScreen : Fragment() {
    lateinit var navController : NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.screen_first_home, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        val btn = view.findViewById<Button>(R.id.first_btn)
        btn.setOnClickListener {
            val action = FirstHomeScreenDirections.actionFirstHomeScreenToSecondHomeScreen()
            navController.navigate(action)
        }
    }
}