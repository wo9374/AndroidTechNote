package com.example.androidtechnote.ktor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityKtorBinding
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class KtorActivity : AppCompatActivity(), CoroutineScope {

    lateinit var binding : ActivityKtorBinding
    lateinit var job : Job

    override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ktor)

        job = Job() // CoroutineScope를 위해 job을 할당


        binding.ktorSimpleBtn.setOnClickListener {
            binding.ktorText.text = "Ktor Api Loading..."

            launch(Dispatchers.Main) {
                val result = HttpRequestHelper().requestKtorIo()
                binding.ktorText.text = result
            }
        }

        binding.ktorDetailBtn.setOnClickListener {
            binding.ktorText.text = "Ktor Api Loading..."

            launch(Dispatchers.Main) {
                val result = HttpRequestHelper().requestKtorIoInDetail()
                binding.ktorText.text = result
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel() //Activity 종료시 job 종료
    }
}