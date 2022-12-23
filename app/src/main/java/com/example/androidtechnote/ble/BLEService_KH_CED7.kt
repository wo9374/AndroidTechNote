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
import java.util.*


class BLEService_KH_CED7 : Service() {

    companion object{
        const val STATE_DISCONNECTED = 0
        const val STATE_CONNECTING = 1
        const val STATE_CONNECTED = 2

        const val ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED"
        const val ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_DISCOVERED"

        const val ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE"
        const val EXTRA_BATTERY = "EXTRA_BATTERY"
        const val EXTRA_HEART_RATE = "EXTRA_HEART_RATE"

        //Notify UUID
        val CLIENT_CHARACTERISTIC_CONFIG_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb") //0x2902

    }

    var connectionState = STATE_DISCONNECTED
    var bluetoothGatt: BluetoothGatt? = null
    var deviceAddress: String = ""
    private val bluetoothAdapter: BluetoothAdapter by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    inner class LocalBinder: Binder() {
        var service = this@BLEService_KH_CED7
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
            DlogUtil.d("ddd", "onCharacteristicChanged ${characteristic?.value}")
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

            when(descriptor?.characteristic?.uuid){

            }
        }
    }

    private fun broadcastUpdate(action:String, characteristic: BluetoothGattCharacteristic? = null){
        val intent = Intent(action)

        characteristic?.let {
            when(it.uuid){

            }
        } ?: sendBroadcast(intent)
    }

}