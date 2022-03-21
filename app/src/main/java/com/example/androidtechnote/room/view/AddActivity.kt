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
            copyClipBoard()  //Fragment 에서는 따로 버튼안에 호출이 필요없이 onCreate에서도 동작할거같다.

            //복사
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
        //context.getSystemService를 통해 ClipboardManager를 가져온다.
        val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // clipboard 내부의 값을 가져온다.
        val clipboardTest = clipboard.plainTextClip()

        // 클립보드 내 값이 유효 && 운송장 번호로 사용할 숫자형태의 문자열인지 && 길이가 20자 이하인지
        if (!clipboardTest.isNullOrBlank() && clipboardTest.length <= 20) { //&& clipboardTest.isDigitsOnly()
            AlertDialog.Builder(this)
                .setTitle("클립 보드에 있는 $clipboardTest 를 붙여넣기")
                .setPositiveButton("추가") { _, _ ->
                    binding.addEdittextTitle.setText(clipboardTest)
                }
                .setNegativeButton("취소") { _, _ -> }
                .create()
                .show()
        }
    }

    // 클립보드 내 값의 유효성 검사
    private fun ClipboardManager.plainTextClip(): String? =
        // 클립보드 내의 값 저장 여부 & 해당 값이 text 형식인지 판단
        if (hasPrimaryClip() && (primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true)) {
            // 클립보드의 text 값을 toString()을 통해 String 형태로 변환하여 return 한다.
            primaryClip?.getItemAt(0)?.text?.toString()
        } else {
            // 클립보드 내 값이 없거나, text 형식이 아닐 시 null return
            null
        }

    //ClipboardManager.OnPrimaryClipChangedListener
    //clipboard의 클립이 변경될 때 호출되는 리스너 콜백을 정의할 수 있다.
    //clearPrimaryClip() - 현재 클립의 내용을 제거합니다.
    //getPrimaryClip() - 현재 클립의 내용을 반환한다.
    //hasPrimaryClip() - 클립이 있는지 없는지에 대한 Boolean을 반환한다.
    //setPrimaryClip(ClipData clip) - clipdata를 설정한다.
}