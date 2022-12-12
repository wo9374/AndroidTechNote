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
import android.graphics.Color
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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class DeviceControlActivity : AppCompatActivity() {

    lateinit var binding: ActivityDeviceControlBinding

    private var deviceAddress: String = ""
    private var bluetoothLeService: BluetoothLeService? = null

    var ecgDisplayChar : BluetoothGattCharacteristic? = null
    var ecgMeasurementChar : BluetoothGattCharacteristic? = null

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

        binding.ecgBtn.setOnClickListener {
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
            }
        }

        binding.ecgGraph.apply {
            extraBottomOffset = 5f
            description.isEnabled = false   // 차트 옆 별도로 표시되는 description
            legend.isEnabled = false        // 차트 아래 색과 라벨을 나타내는 설정 (여러 라인 구분할때)
            setScaleEnabled(false)          // 모든 확대/축소 비활성화
            setDrawGridBackground(false)    // 격자 구조 유무

            // X축 설정
            xAxis.run {
                setDrawAxisLine(false)
                setDrawGridLines(false)
                setDrawLabels(false)
                position = XAxis.XAxisPosition.BOTTOM // x축 데이터 표시 위치
                //setValueFormatter(new ChartXValueFormatter()); //X축의 데이터를 제 가공함. new ChartXValueFormatter은 Custom한 소스
                //setLabelCount(5, true); //X축의 데이터를 최대 몇개 까지 나타낼지에 대한 설정 5개 force가 true 이면 반드시 보여줌
                //textColor = ContextCompat.getColor(context, R.color.textColor); //텍스트 컬러 설정
                //gridColor = ContextCompat.getColor(context, R.color.textColor); //그리드 줄의 컬러 설정

                spaceMin = 0.1f // Chart 맨 왼쪽 간격 띄우기
                spaceMax = 0.1f // Chart 맨 오른쪽 간격 띄우기
            }

            // Y축 왼쪽 설정
            axisLeft.run {
                setDrawAxisLine(false)
                setDrawGridLines(false)
                setDrawLabels(false)

                //axisLineWidth = 2f
                axisMinimum = (-260).toFloat()
                axisMaximum = 260.toFloat()
            }

            // Y축 오른쪽 설정
            axisRight.run {
                isEnabled = false     // 우측 Y축 disabled
            }
        }
    }

    fun createChartData(data : List<Int>){
        val entries = mutableListOf<Entry>()  //차트 데이터 셋에 담겨질 데이터

        data.forEachIndexed { index, i ->
            entries.add(Entry(index.toFloat(), i.toFloat()))

            val set1 = LineDataSet(entries, "")

            set1.color = Color.RED              //Line Color 설정
            set1.setDrawCircles(false)          //Line Dot 설정
            //set1.setCircleColor(Color.BLUE)      //Line Circle Color 설정
            set1.setDrawCircleHole(false)       //Line DotHole 설정
            //set1.circleHoleColor = Color.GREEN   //Line Hole Circle Color 설정

            val lineData = LineData() //LineDataSet 을 담는 그릇 여러개의 라인 데이터가 들어갈 수 있다.
            lineData.addDataSet(set1)
            lineData.apply {
                //setValueTextColor(color: Int)
                //setValueTextSize(size: Float)
            }

            binding.ecgGraph.data = lineData
            binding.ecgGraph.invalidate()
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

                    val logData = intent.getByteArrayExtra(BluetoothLeService.EXTRA_ECG)?.map {
                        String.format("0x%02x ", it).replace("0x","")
                    }?.let {
                        DlogUtil.d("ddd", it)
                    }

                    val dataList = intent.getByteArrayExtra(BluetoothLeService.EXTRA_ECG)?.map {
                        it.toInt()
                    } ?: arrayListOf()
                    createChartData(dataList)
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
                    bluetoothLeService?.bluetoothGatt?.setCharacteristicNotification(batteryChar, true)

                    val descriptor = batteryChar.getDescriptor(BluetoothLeService.CLIENT_CHARACTERISTIC_CONFIG_UUID).apply {
                        value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    }
                    bluetoothLeService?.bluetoothGatt?.writeDescriptor(descriptor)
                }

                BluetoothLeService.HEART_RATE_SERVICE -> {
                    val heartRateChar = gattService.getCharacteristic(BluetoothLeService.HEART_RATE_MEASUREMENT)
                    bluetoothLeService?.bluetoothGatt?.setCharacteristicNotification(heartRateChar, true)

                    val descriptor: BluetoothGattDescriptor = heartRateChar.getDescriptor(BluetoothLeService.CLIENT_CHARACTERISTIC_CONFIG_UUID).apply {
                        value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    }

                    lifecycleScope.launch{
                        delay(300)
                        bluetoothLeService?.bluetoothGatt?.writeDescriptor(descriptor)
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
