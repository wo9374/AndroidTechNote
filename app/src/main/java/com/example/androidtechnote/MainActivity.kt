package com.example.androidtechnote

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
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

    private var mPhoneReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_NEW_OUTGOING_CALL) { // 전화를 걸었을 떄이다. (안드로이드 낮은 버전에서는 여기로 들어온다)
                incomingFlag = false
            }
            // 폰 상태 체크
            processPhoneState(intent, context)
        }
    }

    private fun setPhoneReceiver() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL)
        filter.addAction("android.intent.action.PHONE_STATE")
        this.registerReceiver(mPhoneReceiver, filter)
    }

    private var incomingFlag = false
    private fun processPhoneState(intent: Intent, context: Context) {
        val tm = context.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
        when (tm.callState) {
            TelephonyManager.CALL_STATE_RINGING -> {
                // 전화를 왔을때이다. (벨이 울릴때)
                incomingFlag = true
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                if (incomingFlag) { // 전화가 왔을때이다. (통화 시작)
                    //Log.d("test", "CALL_STATE_OFFHOOK - if");
                } else { // 안드로이드 8.0 으로 테스트했을때는 ACTION_NEW_OUTGOING_CALL 거치지 않고, 이쪽으로 바로 온다.
                    //Log.d("test", "CALL_STATE_OFFHOOK - else");
                }
                if (incomingFlag) { // 전화가 왔고, 통화를 시작했을때 그에 맞는 프로세스를 실행한다.
                    //Log.d("test", "전화가 와서 받았음");
                } else { // 전화를 했을때 그에 맞는 프로세스를 실행한다.
                    //Log.d("test", "전화 했음");
                }
            }
            TelephonyManager.CALL_STATE_IDLE -> if (incomingFlag) { // 전화가 왔을때이다. (전화를 끊었을 경우)
                //Log.d("test", "전화가 왔고, 끊었음");
            } else {
                //  전화를 걸었을 떄이다. (전화를 끊었을 경우)
                //Log.d("test", "전화를 했고, 끊었음");
            }
        }
    }
}