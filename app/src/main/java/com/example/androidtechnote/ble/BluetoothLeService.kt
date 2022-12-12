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

        const val ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED"
        const val ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_DISCOVERED"

        const val ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE"
        const val EXTRA_BATTERY = "EXTRA_BATTERY"
        const val EXTRA_HEART_RATE = "EXTRA_HEART_RATE"
        const val EXTRA_ECG = "EXTRA_ECG"

        //Notify UUID
        val CLIENT_CHARACTERISTIC_CONFIG_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb") //0x2902

        //배터리
        val BATTERY_SERVICE: UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")            //0x180F
        val BATTERY_LEVEL: UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")              //0x2A19

        //심박센서
        val HEART_RATE_SERVICE: UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")        //0x180D
        val HEART_RATE_MEASUREMENT: UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")    //0x2A37
        val HEART_RATE_CONTROL_POINT: UUID = UUID.fromString("00002a39-0000-1000-8000-00805f9b34fb")  //0x2A39

        //심전도센서
        val ECG_MEASUREMENT: UUID = UUID.fromString("be940003-7333-be46-b7ae-689e71722bd5")
        val ECG_CONTROL_POINT: UUID = UUID.fromString("be940002-7333-be46-b7ae-689e71722bd5")


        val WATCH_SERVICE: UUID = UUID.fromString("be940000-7333-be46-b7ae-689e71722bd5")
        val WATCH_WRITE_CHARACTER: UUID = UUID.fromString("be940001-7333-be46-b7ae-689e71722bd5")

        val ECG_VALUE_DISPLAY_ON: ByteArray = BigInteger("030207000264B7", 16).toByteArray()          //ECG 워치 화면 display
        val ECG_DISPLAY_VALUE_2: ByteArray = BigInteger("03090900010302F38B", 16).toByteArray()
        val ECG_VALUE_MEASUREMENT_START: ByteArray = BigInteger("030B08000101DC8A", 16).toByteArray() //ECG 측정 시작


        //sync first
        val FUNCTION_1: ByteArray = BigInteger("03090900000001F3D9", 16).toByteArray()
        //write character for 05:24:00:00:08:2C: service = be940000-7333-be46-b7ae-689e71722bd5, character = be940001-7333-be46-b7ae-689e71722bd5, value = 03090900000001F3D9
        //onCharacteristicChanged for 05:24:00:00:08:2C: value = 03090700003989, service = 0xbe940000-7333-be46-b7ae-689e71722bd5, character = 0xbe940001-7333-be46-b7ae-689e71722bd5
        //reciverMsg： 03,09,07,00,00,39,89

        /** next */
        //sendMsg: 02,00,08,00,47,43,6f,ec, char1UUID: be940001-7333-be46-b7ae-689e71722bd5--mac==05:24:00:00:08:2C
        val FUNCTION_2 = BigInteger("0200080047436FEC", 16).toByteArray() //sync second
        //write character for 05:24:00:00:08:2C: service = be940000-7333-be46-b7ae-689e71722bd5, character = be940001-7333-be46-b7ae-689e71722bd5, value = 0200080047436FEC
        //onCharacteristicChanged for 05:24:00:00:08:2C: value = 02001000A3000403006000010702D844, service = 0xbe940000-7333-be46-b7ae-689e71722bd5, character = 0xbe940001-7333-be46-b7ae-689e71722bd5
        //reciverMsg： 02,00,10,00,a3,00,04,03,00,60,00,01,07,02,d8,44

        /** next */
        //sendMsg: 05,02,07,00,01,82,4a, char1UUID: be940001-7333-be46-b7ae-689e71722bd5--mac==05:24:00:00:08:2C
        //write character for 05:24:00:00:08:2C: service = be940000-7333-be46-b7ae-689e71722bd5, character = be940001-7333-be46-b7ae-689e71722bd5, value = 0502070001824A
        //onCharacteristicChanged for 05:24:00:00:08:2C: value = 050210000100010000000E000000F418, service = 0xbe940000-7333-be46-b7ae-689e71722bd5, character = 0xbe940001-7333-be46-b7ae-689e71722bd5
        //reciverMsg： 05,02,10,00,01,00,01,00,00,00,0e,00,00,00,f4,18

        //onCharacteristicChanged for 05:24:00:00:08:2C: value = 0511140070FE292B78052A2B27001B0001006428, service = 0xbe940000-7333-be46-b7ae-689e71722bd5, character = 0xbe940003-7333-be46-b7ae-689e71722bd5
        //reciverMsg： 05,11,14,00,70,fe,29,2b,78,05,2a,2b,27,00,1b,00,01,00,64,28

        //onCharacteristicChanged for 05:24:00:00:08:2C: value = 05800C0001000E000869901E, service = 0xbe940000-7333-be46-b7ae-689e71722bd5, character = 0xbe940001-7333-be46-b7ae-689e71722bd5
        //reciverMsg： 05,80,0c,00,01,00,0e,00,08,69,90,1e

        /** next */
        //sendMsg: 05,80,07,00,01,d2,7a, char1UUID: be940001-7333-be46-b7ae-689e71722bd5--mac==05:24:00:00:08:2C
        //write character for 05:24:00:00:08:2C: service = be940000-7333-be46-b7ae-689e71722bd5, character = be940001-7333-be46-b7ae-689e71722bd5, value = 0580070001D27A

        /** next */
        //sendMsg: 05,40,07,00,02,15,f9, char1UUID: be940001-7333-be46-b7ae-689e71722bd5--mac==05:24:00:00:08:2C
        //write character for 05:24:00:00:08:2C: service = be940000-7333-be46-b7ae-689e71722bd5, character = be940001-7333-be46-b7ae-689e71722bd5, value = 054007000215F9
        //onCharacteristicChanged for 05:24:00:00:08:2C: value = 054007000057D9, service = 0xbe940000-7333-be46-b7ae-689e71722bd5, character = 0xbe940001-7333-be46-b7ae-689e71722bd5
        //reciverMsg： 05,40,07,00,00,57,d9

        /** next */
        //sendMsg: 05,04,07,00,01,1b,6d, char1UUID: be940001-7333-be46-b7ae-689e71722bd5--mac==05:24:00:00:08:2C
        //write character for 05:24:00:00:08:2C: service = be940000-7333-be46-b7ae-689e71722bd5, character = be940001-7333-be46-b7ae-689e71722bd5, value = 05040700011B6D
        //onCharacteristicChanged for 05:24:00:00:08:2C: value = 050408000000D441, service = 0xbe940000-7333-be46-b7ae-689e71722bd5, character = 0xbe940001-7333-be46-b7ae-689e71722bd5
        //reciverMsg： 05,04,08,00,00,00,d4,41

        /** next */
        //sendMsg: 05,06,07,00,01,73,80, char1UUID: be940001-7333-be46-b7ae-689e71722bd5--mac==05:24:00:00:08:2C
        //write character for 05:24:00:00:08:2C: service = be940000-7333-be46-b7ae-689e71722bd5, character = be940001-7333-be46-b7ae-689e71722bd5, value = 05060700017380
        //onCharacteristicChanged for 05:24:00:00:08:2C: value = 0506080000005705, service = 0xbe940000-7333-be46-b7ae-689e71722bd5, character = 0xbe940001-7333-be46-b7ae-689e71722bd5
        //reciverMsg： 05,06,08,00,00,00,57,05

        /** next */
        //sendMsg: 05,08,07,00,01,29,22, char1UUID: be940001-7333-be46-b7ae-689e71722bd5--mac==05:24:00:00:08:2C
        //write character for 05:24:00:00:08:2C: service = be940000-7333-be46-b7ae-689e71722bd5, character = be940001-7333-be46-b7ae-689e71722bd5, value = 05080700012922
        //onCharacteristicChanged for 05:24:00:00:08:2C: value = 050808000000FFCA, service = 0xbe940000-7333-be46-b7ae-689e71722bd5, character = 0xbe940001-7333-be46-b7ae-689e71722bd5
        //reciverMsg： 05,08,08,00,00,00,ff,ca

        /** next */
        //sendMsg: 05,09,07,00,01,9d,54, char1UUID: be940001-7333-be46-b7ae-689e71722bd5--mac==05:24:00:00:08:2C
        //write character for 05:24:00:00:08:2C: service = be940000-7333-be46-b7ae-689e71722bd5, character = be940001-7333-be46-b7ae-689e71722bd5, value = 05090700019D54
        //onCharacteristicChanged for 05:24:00:00:08:2C: value = 050908000000AE60, service = 0xbe940000-7333-be46-b7ae-689e71722bd5, character = 0xbe940001-7333-be46-b7ae-689e71722bd5
        //reciverMsg： 05,09,08,00,00,00,ae,60

        //chong----------weather----2--6--6--Seoul
        //sendMsg......true
        //03,12,17,00,00,02,00,2d,32,01,01,00,36,02,01,00,36,04,01,00,04,d9,6c,
        //onReceive: 1
        //receive 1.....isConn: true--[3, 18, 23, 0, 0, 2, 0, 45, 50, 1, 1, 0, 54, 2, 1, 0, 54, 4, 1, 0, 4, -39, 108]

        //sendMsg: 03,12,17,00,00,02,00,2d,32,01,01,00,36,02,01,00,36,04,01,00,04,d9,6c, char1UUID: be940001-7333-be46-b7ae-689e71722bd5--mac==05:24:00:00:08:2C
        //write character for 05:24:00:00:08:2C: service = be940000-7333-be46-b7ae-689e71722bd5, character = be940001-7333-be46-b7ae-689e71722bd5, value = 031217000002002D32010100360201003604010004D96C
        //onCharacteristicChanged for 05:24:00:00:08:2C: value = 0312070000818C, service = 0xbe940000-7333-be46-b7ae-689e71722bd5, character = 0xbe940001-7333-be46-b7ae-689e71722bd5
        //reciverMsg： 03,12,07,00,00,81,8c

        /** next */
        //sendMsg: 03,09,09,00,01,00,01,c3,ee, char1UUID: be940001-7333-be46-b7ae-689e71722bd5 realType: 2
        //write character for 05:24:00:00:08:2C: service = be940000-7333-be46-b7ae-689e71722bd5, character = be940001-7333-be46-b7ae-689e71722bd5, value = 03090900010001C3EE
        //onCharacteristicChanged for 05:24:00:00:08:2C: value = 03090700003989, service = 0xbe940000-7333-be46-b7ae-689e71722bd5, character = 0xbe940001-7333-be46-b7ae-689e71722bd5
        //reciverMsg： 03,09,07,00,00,39,89

        /** This Point UI 동기화 추측*/

        //onCharacteristicChanged for 05:24:00:00:08:2C: value = 06000C0027001B000200A21D, service = 0xbe940000-7333-be46-b7ae-689e71722bd5, character = 0xbe940003-7333-be46-b7ae-689e71722bd5
        //reciverMsg： 06,00,0c,00,27,00,1b,00,02,00,a2,1d,
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
            //DlogUtil.d("ddd", "onCharacteristicChanged")
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
                BATTERY_LEVEL -> {
                    DlogUtil.d("ddd", "onDescriptorWrite BATTERY_LEVEL")
                    val characteristic = gatt.getService(BATTERY_SERVICE).getCharacteristic(BATTERY_LEVEL)
                    characteristic.value = byteArrayOf(1, 1)
                    gatt.writeCharacteristic(characteristic)
                }
                HEART_RATE_MEASUREMENT -> {
                    DlogUtil.d("ddd", "onDescriptorWrite HEART_RATE_MEASUREMENT")
                    val characteristic = gatt.getService(HEART_RATE_SERVICE).getCharacteristic(HEART_RATE_CONTROL_POINT)
                    characteristic.value = byteArrayOf(1, 1)
                    gatt.writeCharacteristic(characteristic)
                }

                ECG_MEASUREMENT -> {
                    DlogUtil.d("ddd", "onDescriptorWrite ECG_MEASUREMENT")
                    val characteristic = gatt.getService(WATCH_SERVICE).getCharacteristic(ECG_CONTROL_POINT)
                    characteristic.value = byteArrayOf(1, 1)
                    gatt.writeCharacteristic(characteristic)
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
                    DlogUtil.d("ddd", "broadcastUpdate ECG_MEASUREMENT ${it.value.toList()}")
                    intent.putExtra(EXTRA_ECG, it.value)
                }
                else -> {}
            }
        }
        sendBroadcast(intent)
    }
}