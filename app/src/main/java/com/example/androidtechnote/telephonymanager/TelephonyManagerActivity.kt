package com.example.androidtechnote.telephonymanager

import android.app.Service
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.ServiceState
import android.telephony.TelephonyManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityTelephonyManagerBinding


class TelephonyManagerActivity : AppCompatActivity() {

    lateinit var binding : ActivityTelephonyManagerBinding
    lateinit var mTelephonyManager: TelephonyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_telephony_manager)

        setContentView(binding.root)

        mTelephonyManager = this.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        
        //상수 여러개 지정 가능
        //mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SERVICE_STATE or PhoneStateListener.LISTEN_CALL_STATE)
    }

    override fun onDestroy() {
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE)
        super.onDestroy()
    }

    private val mPhoneStateListener = object : PhoneStateListener(){
        //통화 상태 변경
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            when(state){
                TelephonyManager.CALL_STATE_RINGING -> {
                    // 벨이 울릴때
                    binding.idText.text = "전화 수신 (벨 울림)"
                }

                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    // 통화중 일때
                    binding.idText.text = "통화중"
                }

                TelephonyManager.CALL_STATE_IDLE -> {
                    binding.idText.text = "통화대기상태"
                }
            }
            super.onCallStateChanged(state, phoneNumber)
        }

        //단말기의 서비스 상태 변경
        override fun onServiceStateChanged(serviceState: ServiceState?) {
            when (serviceState?.state) {
                ServiceState.STATE_EMERGENCY_ONLY -> Log.d("bum", "긴급 통화만 가능한 상태...")
                ServiceState.STATE_OUT_OF_SERVICE -> Log.d("bum", "서비스 불가 상태...")
                ServiceState.STATE_POWER_OFF -> Log.d("bum", "전화 기능을 꺼 놓은 상태...")
                ServiceState.STATE_IN_SERVICE -> Log.d("bum", "서비스 가능 상태...")
            }
            super.onServiceStateChanged(serviceState)
        }
    }
}