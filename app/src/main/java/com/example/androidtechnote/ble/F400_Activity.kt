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
import android.view.View
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


class F400_Activity : AppCompatActivity() {

    lateinit var binding: ActivityDeviceControlBinding

    private var deviceAddress: String = ""
    private var BLEServiceF400: BLEService_F400? = null

    var writeWatchChar : BluetoothGattCharacteristic? = null
    var ecgMeasurementChar : BluetoothGattCharacteristic? = null

    var arrayDeque = ArrayDeque<ArrayList<Int>>(3)

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            BLEServiceF400 = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            BLEServiceF400 = (service as BLEService_F400.LocalBinder).service
            BLEServiceF400?.connect(deviceAddress)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_device_control)

        deviceAddress = intent.getStringExtra("address") ?: ""

        binding.callBack = callBack

        val gattServiceIntent = Intent(this, BLEService_F400::class.java)
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        initialECGFunction()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter())

        if (BLEServiceF400 != null) {
            val result: Boolean = BLEServiceF400!!.connect(deviceAddress)
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
        BLEServiceF400 = null
    }

    var connected = false
    private val gattUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BLEService_F400.ACTION_GATT_CONNECTED -> {
                    DlogUtil.d("ddd", "gattUpdateReceiver BluetoothLeService.ACTION_GATT_CONNECTED")
                    connected = true
                    BLEServiceF400?.discoverService()
                }
                BLEService_F400.ACTION_GATT_DISCONNECTED -> {
                    connected = false
                    BLEServiceF400?.disconnect()
                    Toast.makeText(this@F400_Activity, "BLE: 연결 해제", Toast.LENGTH_SHORT).show()
                }
                BLEService_F400.ACTION_GATT_SERVICES_DISCOVERED -> {
                    DlogUtil.d("ddd", "gattUpdateReceiver ACTION_GATT_SERVICES_DISCOVERED")
                    BLEServiceF400?.let {
                        selectCharacteristicData(it.bluetoothGatt?.services)
                    }
                }
                BLEService_F400.ACTION_DATA_AVAILABLE -> {
                    //DlogUtil.d("ddd", "gattUpdateReceiver ACTION_DATA_AVAILABLE")

                    val batteryPercent = intent.getIntExtra(BLEService_F400.EXTRA_BATTERY, -1)
                    if (batteryPercent != -1){
                        DlogUtil.d("ddd", batteryPercent)
                        binding.battery.setText("$batteryPercent%")
                    }

                    val heartRate = intent.getStringExtra(BLEService_F400.EXTRA_HEART_RATE)
                    heartRate?.let {
                        if (it == "0") {
                            //binding.heartRate.setText("Enter The Heart Rate Mode")
                        }else{
                            binding.heartRate.setText("$it bpm")
                        }
                    }

                    //ecg 그래프
                    intent.getIntegerArrayListExtra(BLEService_F400.EXTRA_ECG)?.let { intentData ->
                        /*preEcgDataList.addAll(it)
                        createChartData(preEcgDataList)
                        preEcgDataList = it*/
                        if (arrayDeque.size < 3){
                            arrayDeque.addLast(intentData)
                        }else{
                            arrayDeque.removeFirst()
                            arrayDeque.addLast(intentData)
                        }

                        val threeData = arrayListOf<Int>()
                        arrayDeque.forEach {
                            threeData.addAll(it)
                        }
                        createChartData(threeData)
                    }
                }
            }
        }
    }

    //심전도 관련 Initial
    private fun initialECGFunction(){
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
                axisMinimum = (-2000).toFloat()
                axisMaximum = 2000.toFloat()
            }

            // Y축 오른쪽 설정
            axisRight.run {
                isEnabled = false     // 우측 Y축 disabled
            }
        }
    }

    private fun selectCharacteristicData(gattServices: List<BluetoothGattService>?) {
        if (gattServices == null) return

        gattServices.forEach { gattService ->
            when (gattService.uuid) {
                BLEService_F400.BATTERY_SERVICE -> {
                    DlogUtil.d("ddd", "selectCharacteristicData BATTERY_SERVICE")
                    val batteryChar = gattService.getCharacteristic(BLEService_F400.BATTERY_LEVEL)
                    //setDescriptor(batteryChar, false)
                }

                BLEService_F400.HEART_RATE_SERVICE -> {
                    DlogUtil.d("ddd", "selectCharacteristicData HEART_RATE_SERVICE")
                    val heartRateChar = gattService.getCharacteristic(BLEService_F400.HEART_RATE_MEASUREMENT)
                    lifecycleScope.launch{
                        delay(300)
                        //setDescriptor(heartRateChar, false)
                    }
                }

                BLEService_F400.WATCH_SERVICE -> {
                    DlogUtil.d("ddd", "selectCharacteristicData ECG_SERVICE")
                    writeWatchChar = gattService.getCharacteristic(BLEService_F400.WATCH_WRITE_CHARACTER)
                    ecgMeasurementChar = gattService.getCharacteristic(BLEService_F400.ECG_MEASUREMENT)
                }
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
                setDrawValues(false)
            }

            binding.ecgGraph.data = lineData
            binding.ecgGraph.invalidate()
        }
    }

    private fun makeGattUpdateIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter().apply {
            addAction(BLEService_F400.ACTION_GATT_CONNECTED)
            addAction(BLEService_F400.ACTION_GATT_DISCONNECTED)
            addAction(BLEService_F400.ACTION_GATT_SERVICES_DISCOVERED)
            addAction(BLEService_F400.ACTION_DATA_AVAILABLE)
        }
        return intentFilter
    }

    private fun writeWatchCharacteristic(writeValue: ByteArray){
        writeWatchChar?.let {  writeWatch ->
            writeWatch.apply {
                value = writeValue
                writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
                BLEServiceF400?.bluetoothGatt?.writeCharacteristic(this)
            }
        }
    }
    private fun setDescriptor(char: BluetoothGattCharacteristic, indicateEnabled: Boolean){
        BLEServiceF400?.bluetoothGatt?.setCharacteristicNotification(char, true)
        val descriptor: BluetoothGattDescriptor = char.getDescriptor(BLEService_F400.CLIENT_CHARACTERISTIC_CONFIG_UUID).apply {
            value =
                if (indicateEnabled) BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                else BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        }
        BLEServiceF400?.bluetoothGatt?.writeDescriptor(descriptor)
    }

    interface Callback{ fun onClick(view: View) }

    private val callBack = object : Callback{
        override fun onClick(view: View) {
            binding.apply {
                when(view.id){
                    ecgBtn.id -> {
                        writeWatchCharacteristic(BLEService_F400.ECG_VALUE_DISPLAY_ON)
                        writeWatchCharacteristic(BLEService_F400.ECG_VALUE_MEASUREMENT_START)

                        ecgMeasurementChar?.let {
                            setDescriptor(it, true)
                        }
                    }

                    theme1.id -> writeWatchCharacteristic(BLEService_F400.WATCH_THEME_1)
                    theme2.id -> writeWatchCharacteristic(BLEService_F400.WATCH_THEME_2)
                    theme3.id -> writeWatchCharacteristic(BLEService_F400.WATCH_THEME_3)
                    theme4.id -> writeWatchCharacteristic(BLEService_F400.WATCH_THEME_4)
                    theme5.id -> writeWatchCharacteristic(BLEService_F400.WATCH_THEME_5)

                    bloodBtn.id ->{
                        writeWatchCharacteristic(BLEService_F400.BLOOD_PRESSURE_VALUE_DISPLAY_MEASUREMENT)
                    }
                }
            }
        }
    }
}
