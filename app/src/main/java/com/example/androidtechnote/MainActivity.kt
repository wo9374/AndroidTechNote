package com.example.androidtechnote

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidtechnote.camera.camerakit_library.CameraXActivity
import com.example.androidtechnote.coordinator.bottomsheet.BottomSheetActivity
import com.example.androidtechnote.coordinator.CoordinatorActivity
import com.example.androidtechnote.databinding.ActivityMainBinding
import com.example.androidtechnote.ktor.KtorActivity
import com.example.androidtechnote.myworkmanager.WorkManagerActivity
import com.example.androidtechnote.navigation.NavigationActivity
import com.example.androidtechnote.recycler.epoxy.EpoxyActivity
import com.example.androidtechnote.recycler.paging3.PagingActivity
import com.example.androidtechnote.room.view.RoomDbActivity
import com.example.androidtechnote.viewpager2.ViewPager2Activity

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        //onClick interface Set
        binding.callBack = callBack

        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    interface Callback{
        fun onClick(view : View)
    }

    private val callBack = object : Callback{
        override fun onClick(view : View){
            var intent = Intent()

            when(view.id){
                binding.naviBtn.id -> intent =Intent(applicationContext, NavigationActivity::class.java)
                binding.coordinatorBtn.id -> intent =Intent(applicationContext, CoordinatorActivity::class.java)
                binding.workBtn.id -> intent =Intent(applicationContext, WorkManagerActivity::class.java)
                binding.pagingBtn.id -> intent = Intent(applicationContext, PagingActivity::class.java)
                binding.cameraBtn.id -> intent = Intent(applicationContext, CameraXActivity::class.java)
                binding.roomBtn.id -> intent = Intent(applicationContext, RoomDbActivity::class.java )
                binding.epoxyBtn.id -> intent = Intent(applicationContext, EpoxyActivity::class.java )
                binding.ktorBtn.id -> intent = Intent(applicationContext, KtorActivity::class.java )
                binding.viewpagerBtn.id -> intent = Intent(applicationContext, ViewPager2Activity::class.java )
                binding.bottomSheetBtn.id -> intent = Intent(applicationContext, BottomSheetActivity::class.java)
            }
            startActivity(intent)
        }
    }
}