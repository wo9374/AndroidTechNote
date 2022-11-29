package com.example.androidtechnote.ble

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.androidtechnote.R
import com.example.androidtechnote.databinding.ActivityBleScanBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BLEScanActivity : AppCompatActivity() {

    lateinit var binding: ActivityBleScanBinding

    private fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)

    // 1. BluetoothAdapter 가져오기
    private val bluetoothAdapter: BluetoothAdapter by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ble_scan)

        checkPermission()
    }

    override fun onResume() {
        super.onResume()

        packageManager.takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) }?.also {
            Toast.makeText(this, "BLE 지원 불가", Toast.LENGTH_SHORT).show()
            finish()
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && bluetoothAdapter.isEnabled){
            scanLeDevice(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_ble, menu)
        if (!mScanning) {
            menu.apply {
                findItem(R.id.menu_stop).isVisible = false
                findItem(R.id.menu_scan).isVisible = true
                findItem(R.id.menu_refresh).actionView = null
            }
        } else {
            menu.apply {
                findItem(R.id.menu_stop).isVisible = true
                findItem(R.id.menu_scan).isVisible = false
                findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_indeterminate_progress)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_scan -> scanLeDevice(true)
            R.id.menu_stop -> scanLeDevice(false)
        }
        return true
    }

    // 2.위치 권한 체크
    private val PERMISSION_REQUEST_CODE = 0
    private fun checkPermission(){
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        var arrayPermission = ArrayList<String>()
        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            arrayPermission.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (arrayPermission.size > 0){
            ActivityCompat.requestPermissions(this, arrayPermission.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_REQUEST_CODE ->{
                for (result in grantResults){
                    if (result != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this,"권한 거부됨", Toast.LENGTH_SHORT).show()
                    }else{
                        checkBluetooth()
                    }
                }
            }
        }
    }

    //3. 블루투스 활성화 / isEnabled()를 호출하여 현재 블루투스가 활성화되었는지 확인 (false 시 비활성화 상태)
    private val BluetoothAdapter.isDisabled: Boolean
        get() = !isEnabled

    //4. 장치에서 Bluetooth를 사용할 수 있고 사용하도록 설정되어 있는지 확인.
    private fun checkBluetooth(){
        //활성화되어 있지 않다면 오류를 표시하고 사용자에게 설정으로 가서 블루투스를 활성화하라는 메시지
        bluetoothAdapter.takeIf { it.isDisabled }?.apply {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            bluetoothOnOffResult.launch(enableBtIntent)
        }
    }
    private val bluetoothOnOffResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            scanLeDevice(true)
        } else {
            checkBluetooth()
        }
    }

    // 5. 블루투스 스캔/콜백
    private val handler = Handler(Looper.myLooper()!!)
    private var mScanning: Boolean = false
    private var arrayDevices = ArrayList<BluetoothDevice>()

    private val leScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.let {
                if (!arrayDevices.contains(it.device))
                    arrayDevices.add(it.device)
            }
        }
        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            results?.let {
                for (result in it){
                    if (!arrayDevices.contains(result.device))
                        arrayDevices.add(result.device)
                }
            }
        }
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Toast.makeText(this@BLEScanActivity,"스캔 실패", Toast.LENGTH_SHORT).show()
        }
    }

    private val scanPeriod: Long = 10000
    private fun scanLeDevice(enable: Boolean){
        when (enable) {
            true -> {
                lifecycleScope.launch {
                    delay(scanPeriod)

                    mScanning = false
                    bluetoothAdapter.bluetoothLeScanner.stopScan(leScanCallback)
                    invalidateOptionsMenu()

                    Log.d("스캔 완료","찾은 Device: ${arrayDevices.size}")
                }

                // 미리 정의된 검색 기간 후 검색을 중지
                /*handler.postDelayed({
                    mScanning = false
                    bluetoothAdapter.bluetoothLeScanner.stopScan(leScanCallback)
                    invalidateOptionsMenu()

                    Log.d("스캔 완료","찾은 Device: ${arrayDevices.size}")
                }, scanPeriod)*/

                mScanning = true
                arrayDevices.clear()
                bluetoothAdapter.bluetoothLeScanner.startScan(leScanCallback)
            }
            else -> {
                mScanning = false
                bluetoothAdapter.bluetoothLeScanner.stopScan(leScanCallback)
            }
        }
        invalidateOptionsMenu()
    }

    /*private fun selectDevice(position: Int){
        val device = arrayDevices[position]
        val intent = Intent(this, DeviceControlActivity::class.java)
        intent.putExtra("address", device.address)

        if (mScanning) scanLeDevice(false)
        startActivity(intent)
    }*/
}