package com.example.androidtechnote.myworkmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityWorkManagerBinding
import com.example.androidtechnote.myworkmanager.basicwork.BasicWorkManagerActivity
import com.example.androidtechnote.myworkmanager.videodown.FileDownActivity

class WorkManagerActivity : AppCompatActivity() {
    lateinit var binding : ActivityWorkManagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_work_manager)

        binding.basicWorkBtn.setOnClickListener {
            val intent = Intent(this, BasicWorkManagerActivity::class.java)
            startActivity(intent)
        }

        binding.downWorkBtn.setOnClickListener {
            val intent = Intent(this, FileDownActivity::class.java)
            startActivity(intent)
        }
    }
}