package com.example.androidtechnote.room.view

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
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

    lateinit var binding : ActivityAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        binding =  DataBindingUtil.setContentView(this, R.layout.activity_add)

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

        binding.clipboardBtn.setOnClickListener {
            copyClipBoard()  //Fragment ????????? ?????? ???????????? ????????? ???????????? onCreate????????? ??????????????????.

            //??????
            /*val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", binding.addEdittextTitle.text)
            clipboard.setPrimaryClip(clip)*/
        }

    }

    companion object{
        const val EXTRA_TODO_TITLE = "EXTRA_TODO_TITLE"
        const val EXTRA_TODO_DESC = "EXTRA_TODO_DESC"
        const val EXTRA_TODO_ID = "EXTRA_TODO_ID"
    }

    private fun copyClipBoard() {
        //context.getSystemService??? ?????? ClipboardManager??? ????????????.
        val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // clipboard ????????? ?????? ????????????.
        val clipboardTest = clipboard.plainTextClip()

        // ???????????? ??? ?????? ?????? && ????????? 20??? ????????????
        if (!clipboardTest.isNullOrBlank() && clipboardTest.length <= 20) { //&& clipboardTest.isDigitsOnly()
            AlertDialog.Builder(this)
                .setTitle("?????? ????????? ?????? $clipboardTest ??? ????????????")
                .setPositiveButton("??????") { _, _ ->
                    binding.addEdittextTitle.setText(clipboardTest)
                }
                .setNegativeButton("??????") { _, _ -> }
                .create()
                .show()
        }
    }

    // ???????????? ??? ?????? ????????? ??????
    private fun ClipboardManager.plainTextClip(): String? =
        // ???????????? ?????? ??? ?????? ?????? & ?????? ?????? text ???????????? ??????
        if (hasPrimaryClip() && (primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true)) {
            // ??????????????? text ?????? toString()??? ?????? String ????????? ???????????? return ??????.
            primaryClip?.getItemAt(0)?.text?.toString()
        } else {
            // ???????????? ??? ?????? ?????????, text ????????? ?????? ??? null return
            null
        }

    //addPrimaryClipChangedListener() - ClipboardManager.OnPrimaryClipChangedListener clipboard??? ????????? ????????? ??? ???????????? ????????? ????????? ????????? ??? ??????.
    //clearPrimaryClip() - ?????? ????????? ????????? ??????
    //getPrimaryClip() - ?????? ????????? ????????? ??????
    //hasPrimaryClip() - ????????? ????????? ???????????? ?????? Boolean??? ??????
    //setPrimaryClip(ClipData clip) - clipdata??? ??????
}