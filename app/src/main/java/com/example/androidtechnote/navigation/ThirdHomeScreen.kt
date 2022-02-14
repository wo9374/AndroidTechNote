package com.example.androidtechnote.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.androidtechnote.R

class ThirdHomeScreen : Fragment() {
    lateinit var navController : NavController
    var count = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.screen_third_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        count++

        Log.d("프래그먼트", "$count")

        val btn = view.findViewById<Button>(R.id.third_btn)
        btn.setOnClickListener {
            navController.navigate(R.id.action_thirdHomeScreen_to_firstHomeScreen)
        }
    }
}