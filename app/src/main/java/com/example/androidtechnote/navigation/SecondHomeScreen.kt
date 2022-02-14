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

class SecondHomeScreen : Fragment() {
    lateinit var navController : NavController
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.screen_second_home, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        //val outputArgs : FirstHomeScreenArgs by navArgs()
        //val stepNumber = outputArgs.stepnum

        //Log.d("프래그먼트", "$stepNumber")

        val btn = view.findViewById<Button>(R.id.second_btn)
        btn.setOnClickListener {
            navController.navigate(R.id.action_secondHomeScreen_to_thirdHomeScreen)
        }
    }
}