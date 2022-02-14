package com.example.androidtechnote.room.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityRoomDbBinding
import com.example.androidtechnote.room.model.Test
import com.example.androidtechnote.room.viewmodel.RoomDbViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoomDbActivity : AppCompatActivity() {

    lateinit var binding: ActivityRoomDbBinding

    private val viewModel: RoomDbViewModel by viewModels{
        object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                RoomDbViewModel(application) as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_room_db)
        binding.viewModel = viewModel


        val adapter = TestAdapter({ test -> deleteDialog(test)}, { test -> deleteDialog(test)})
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)

        viewModel.getAll().observe(this, Observer {
            adapter.setTestItemList(it)
        })

        binding.mainButton.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }
    }

    private fun deleteDialog(test: Test) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("삭제/편집 하시겠습니까?")
            .setNegativeButton("취소") { _, _ -> }
            .setPositiveButton("편집") { _, _ ->
                val intent = Intent(this, AddActivity::class.java)
                intent.putExtra(AddActivity.EXTRA_TODO_TITLE, test.title)
                intent.putExtra(AddActivity.EXTRA_TODO_DESC, test.description)
                intent.putExtra(AddActivity.EXTRA_TODO_ID, test.id)
                startActivity(intent)
            }.setNeutralButton("삭제"){_, _ ->
                lifecycleScope.launch(Dispatchers.IO){viewModel.delete(test)}
            }
        builder.show()
    }
}