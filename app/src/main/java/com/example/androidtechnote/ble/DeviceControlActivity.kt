package com.example.androidtechnote.ble

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.androidtechnote.DlogUtil
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityDeviceControlBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DeviceControlActivity : AppCompatActivity() {

    lateinit var binding: ActivityDeviceControlBinding

    private var deviceAddress: String = ""
    private var bluetoothLeService: BluetoothLeService? = null

    var ecgDisplayChar : BluetoothGattCharacteristic? = null
    var ecgMeasurementChar : BluetoothGattCharacteristic? = null

    var batteryService = false
    var heartService = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            bluetoothLeService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            bluetoothLeService = (service as BluetoothLeService.LocalBinder).service
            bluetoothLeService?.connect(deviceAddress)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_device_control)

        deviceAddress = intent.getStringExtra("address") ?: ""

        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        binding.ecgDisplay.setOnClickListener {
            ecgDisplayChar?.let { ecgDisplay ->
                ecgDisplay.apply {
                    value = BluetoothLeService.ECG_DISPLAY_VALUE_1
                    writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
                    bluetoothLeService?.bluetoothGatt?.writeCharacteristic(this)
                }

                ecgDisplay.apply {
                    value = BluetoothLeService.ECG_DISPLAY_VALUE_3
                    bluetoothLeService?.bluetoothGatt?.writeCharacteristic(ecgDisplayChar)
                }
            }

            ecgMeasurementChar?.let { ecgMeasure ->
                bluetoothLeService?.bluetoothGatt?.setCharacteristicNotification(ecgMeasure, true)
                val descriptor: BluetoothGattDescriptor = ecgMeasure.getDescriptor(BluetoothLeService.CLIENT_CHARACTERISTIC_CONFIG_UUID).apply {
                    value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                }

                bluetoothLeService?.bluetoothGatt?.writeDescriptor(descriptor)
                /*lifecycleScope.launch {
                    delay(300)
                    bluetoothLeService?.bluetoothGatt?.writeDescriptor(descriptor)
                }*/
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter())

        if (bluetoothLeService != null) {
            val result: Boolean = bluetoothLeService!!.connect(deviceAddress)
            Log.d(this.javaClass.name, "Connect request result=$result")
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(gattUpdateReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
        bluetoothLeService = null
    }

    var connected = false
    private val gattUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothLeService.ACTION_GATT_CONNECTED -> {
                    DlogUtil.d("ddd", "BluetoothLeService.ACTION_GATT_CONNECTED")
                    connected = true
                    bluetoothLeService?.discoverService()
                }
                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    connected = false
                    bluetoothLeService?.disconnect()
                    Toast.makeText(this@DeviceControlActivity, "BLE: 연결 해제", Toast.LENGTH_SHORT).show()
                }
                BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED -> {
                    DlogUtil.d("ddd", "gattUpdateReceiver ACTION_GATT_SERVICES_DISCOVERED")
                    bluetoothLeService?.let {
                        selectCharacteristicData(it.bluetoothGatt?.services)
                    }
                }
                BluetoothLeService.ACTION_DATA_AVAILABLE -> {
                    //DlogUtil.d("ddd", "gattUpdateReceiver ACTION_DATA_AVAILABLE")

                    val batteryPercent = intent.getIntExtra(BluetoothLeService.EXTRA_BATTERY, -1)
                    if (batteryPercent != -1){
                        DlogUtil.d("ddd", batteryPercent)
                        binding.battery.setText("$batteryPercent%")
                    }

                    val heartRate = intent.getStringExtra(BluetoothLeService.EXTRA_HEART_RATE)
                    heartRate?.let {
                        if (it == "0") {
                            //binding.heartRate.setText("Enter The Heart Rate Mode")
                        }else{
                            binding.heartRate.setText(it)
                        }
                    }
                }
            }
        }
    }

    private fun selectCharacteristicData(gattServices: List<BluetoothGattService>?) {
        if (gattServices == null) return

        gattServices.forEach { gattService ->
            when (gattService.uuid) {
                BluetoothLeService.BATTERY_SERVICE -> {
                    DlogUtil.d("ddd", "selectCharacteristicData BATTERY_SERVICE")
                    val batteryChar = gattService.getCharacteristic(BluetoothLeService.BATTERY_LEVEL)
                    if (batteryService) bluetoothLeService?.bluetoothGatt?.setCharacteristicNotification(batteryChar, true)

                    val descriptor = batteryChar.getDescriptor(BluetoothLeService.CLIENT_CHARACTERISTIC_CONFIG_UUID).apply {
                        value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    }
                    if (batteryService) bluetoothLeService?.bluetoothGatt?.writeDescriptor(descriptor)
                }

                BluetoothLeService.HEART_RATE_SERVICE -> {
                    val heartRateChar = gattService.getCharacteristic(BluetoothLeService.HEART_RATE_MEASUREMENT)
                    if (heartService) bluetoothLeService?.bluetoothGatt?.setCharacteristicNotification(heartRateChar, true)

                    val descriptor: BluetoothGattDescriptor = heartRateChar.getDescriptor(BluetoothLeService.CLIENT_CHARACTERISTIC_CONFIG_UUID).apply {
                        value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    }
                    lifecycleScope.launch {
                        delay(300)
                        if (heartService) bluetoothLeService?.bluetoothGatt?.writeDescriptor(descriptor)
                        DlogUtil.d("ddd", "selectCharacteristicData HEART_RATE_SERVICE")
                    }
                }

                BluetoothLeService.ECG_SERVICE -> {
                    DlogUtil.d("ddd", "selectCharacteristicData ECG_SERVICE")
                    ecgDisplayChar = gattService.getCharacteristic(BluetoothLeService.ECG_DISPLAY)
                    ecgMeasurementChar = gattService.getCharacteristic(BluetoothLeService.ECG_MEASUREMENT)
                }
            }
        }
    }

    private fun makeGattUpdateIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter().apply {
            addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
            addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
            addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)
            addAction(BluetoothLeService.ACTION_DATA_AVAILABLE)
        }
        return intentFilter
    }
}
