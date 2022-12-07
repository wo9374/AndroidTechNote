package com.example.androidtechnote.ble

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.androidtechnote.DlogUtil
import java.math.BigInteger
import java.util.*

class BluetoothLeService : Service() {

    companion object{
        const val STATE_DISCONNECTED = 0
        const val STATE_CONNECTING = 1
        const val STATE_CONNECTED = 2

        val ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED"
        val ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED"
        val ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_DISCOVERED"

        val ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE"
        val EXTRA_BATTERY = "EXTRA_BATTERY"
        val EXTRA_HEART_RATE = "EXTRA_HEART_RATE"

        //Notify UUID
        val CLIENT_CHARACTERISTIC_CONFIG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb") //0x2902

        //배터리
        val BATTERY_SERVICE= UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")            //0x180F
        val BATTERY_LEVEL= UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")              //0x2A19

        //심박센서
        val HEART_RATE_SERVICE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")        //0x180D
        val HEART_RATE_MEASUREMENT = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")    //0x2A37
        val HEART_RATE_CONTROL_POINT = UUID.fromString("00002a39-0000-1000-8000-00805f9b34fb")  //0x2A39

        //심전도센서
        val ECG_SERVICE = UUID.fromString("be940000-7333-be46-b7ae-689e71722bd5")
        val ECG_MEASUREMENT = UUID.fromString("be940001-7333-be46-b7ae-689e71722bd5")
        val ECG_CONTROL_POINT = UUID.fromString("be940003-7333-be46-b7ae-689e71722bd5")

        val ECG_DISPLAY_VALUE_1 = BigInteger("030207000264B7", 16).toByteArray()
        val ECG_DISPLAY_VALUE_2 = BigInteger("03090900010302F38B", 16).toByteArray()
        val ECG_DISPLAY_VALUE_3 = BigInteger("030B08000101DC8A", 16).toByteArray()

    }

    var connectionState = STATE_DISCONNECTED
    var bluetoothGatt: BluetoothGatt? = null
    var deviceAddress: String = ""
    private val bluetoothAdapter: BluetoothAdapter by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    inner class LocalBinder: Binder() {
        var service = this@BluetoothLeService
    }

    val binder = LocalBinder()
    override fun onBind(intent: Intent?): IBinder = binder

    override fun onUnbind(intent: Intent?): Boolean {
        close()
        return super.onUnbind(intent)
    }

    fun discoverService(){ bluetoothGatt?.discoverServices() }

    fun disconnect(){ bluetoothGatt?.disconnect() }

    fun close(){
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    fun connect(address: String): Boolean{
        bluetoothGatt?.let {
            if (address == deviceAddress){
                if (it.connect()){
                    connectionState = STATE_CONNECTING
                    return true
                } else
                    return false
            }
        }

        val device = bluetoothAdapter.getRemoteDevice(address)
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
        deviceAddress = address
        connectionState = STATE_CONNECTING
        return true
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when(newState){
                BluetoothProfile.STATE_CONNECTED -> {
                    connectionState = STATE_CONNECTED
                    broadcastUpdate(ACTION_GATT_CONNECTED)
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    connectionState = STATE_DISCONNECTED
                    broadcastUpdate(ACTION_GATT_DISCONNECTED)
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            DlogUtil.d("ddd", "onServicesDiscovered")
            when(status){
                BluetoothGatt.GATT_SUCCESS -> broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            DlogUtil.d("ddd", "onCharacteristicChanged")
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            DlogUtil.d("ddd", "onCharacteristicRead")
            when(status){
                BluetoothGatt.GATT_SUCCESS -> broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
            }
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            DlogUtil.d("ddd", "onDescriptorWrite")

            if (gatt?.services == null) return

            gatt.services.forEach{ gattService ->
                when (gattService.uuid){
                    BATTERY_SERVICE -> {
                        DlogUtil.d("ddd", "onDescriptorWrite BATTERY_SERVICE")
                        val characteristic = gattService.getCharacteristic(BATTERY_LEVEL)
                        characteristic.value = byteArrayOf(1, 1)
                        gatt.writeCharacteristic(characteristic)
                    }
                    HEART_RATE_SERVICE -> {
                        DlogUtil.d("ddd", "onDescriptorWrite HEART_RATE_SERVICE")
                        val characteristic = gattService.getCharacteristic(HEART_RATE_CONTROL_POINT)
                        characteristic.value = byteArrayOf(1, 1)
                        gatt.writeCharacteristic(characteristic)
                    }

                    ECG_SERVICE -> {
                        DlogUtil.d("ddd", "onDescriptorWrite ECG_SERVICE")
                        val characteristic = gattService.getCharacteristic(ECG_CONTROL_POINT)
                        characteristic.value = byteArrayOf(1, 1)
                        gatt.writeCharacteristic(characteristic)
                    }
                }
            }
        }
    }

    private fun broadcastUpdate(action:String, characteristic: BluetoothGattCharacteristic? = null){
        val intent = Intent(action)

        characteristic?.let {
            when(it.uuid){
                BATTERY_LEVEL -> {
                    DlogUtil.d("ddd", "broadcastUpdate BATTERY_LEVEL")
                    val data = it.value.first().toInt()
                    intent.putExtra(EXTRA_BATTERY, data)
                }

                HEART_RATE_MEASUREMENT -> {
                    DlogUtil.d("ddd", "broadcastUpdate HEART_RATE_MEASUREMENT")
                    val data = it.value[1].toString()
                    intent.putExtra(EXTRA_HEART_RATE, data)
                }
                ECG_MEASUREMENT -> {
                    DlogUtil.d("ddd", "broadcastUpdate ECG_MEASUREMENT")
                }
                else -> {}
            }
        }
        sendBroadcast(intent)
    }
}