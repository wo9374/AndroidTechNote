package com.example.androidtechnote.room.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityAddBinding
import com.example.androidtechnote.room.model.Test
import com.example.androidtechnote.room.viewmodel.RoomDbViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddActivity : AppCompatActivity() {
    private val viewModel: RoomDbViewModel by viewModels{
        object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                RoomDbViewModel(application) as T
        }
    }

    private var id: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val binding =  DataBindingUtil.setContentView<ActivityAddBinding>(this, R.layout.activity_add)

        if(intent!=null
            && intent.hasExtra(EXTRA_TODO_TITLE)
            && intent.hasExtra(EXTRA_TODO_DESC)
            && intent.hasExtra(EXTRA_TODO_ID)
        ){
            binding.addEdittextTitle.setText((intent.getStringExtra(EXTRA_TODO_TITLE)))
            binding.addEdittextDescript.setText(intent.getStringExtra(EXTRA_TODO_DESC))
            id=intent.getIntExtra(EXTRA_TODO_ID, -1)
        }

        binding.addButton.setOnClickListener {
            if(binding.addEdittextTitle.text.isNotEmpty() && binding.addEdittextDescript.text.isNotEmpty()){
                val test = Test(id, binding.addEdittextTitle.text.toString(), binding.addEdittextDescript.text.toString(),"")
                lifecycleScope.launch(Dispatchers.IO){viewModel.insert(test)}
                finish()
            }else{
                Toast.makeText(this,"Please enter title and desc", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object{
        const val EXTRA_TODO_TITLE = "EXTRA_TODO_TITLE"
        const val EXTRA_TODO_DESC = "EXTRA_TODO_DESC"
        const val EXTRA_TODO_ID = "EXTRA_TODO_ID"
    }
}