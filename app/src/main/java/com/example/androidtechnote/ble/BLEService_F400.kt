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


class BLEService_F400 : Service() {

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
        const val EXTRA_ECG_T = "EXTRA_ECG_T"


        val WATCH_SERVICE: UUID = UUID.fromString("be940000-7333-be46-b7ae-689e71722bd5")
        val WATCH_WRITE_CHARACTER: UUID = UUID.fromString("be940001-7333-be46-b7ae-689e71722bd5")

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

        val ECG_VALUE_DISPLAY_ON: ByteArray = BigInteger("030207000264B7", 16).toByteArray()          //ECG 워치화면 display
        val ECG_DISPLAY_VALUE_2: ByteArray = BigInteger("03090900010302F38B", 16).toByteArray()
        val ECG_VALUE_MEASUREMENT_START: ByteArray = BigInteger("030B08000101DC8A", 16).toByteArray() //ECG 측정 시작

        val BLOOD_PRESSURE_VALUE_DISPLAY_MEASUREMENT : ByteArray = BigInteger("03030800735A540D", 16).toByteArray() //혈압 워치화면 display 및 혈압 측정

        //테마 value
        val WATCH_THEME_1 : ByteArray = BigInteger("01190700001DD6", 16).toByteArray()
        val WATCH_THEME_2 : ByteArray = BigInteger("01190700013CC6", 16).toByteArray()
        val WATCH_THEME_3 : ByteArray = BigInteger("01190700025FF6", 16).toByteArray()
        val WATCH_THEME_4 : ByteArray = BigInteger("01190700037EE6", 16).toByteArray()
        val WATCH_THEME_5 : ByteArray = BigInteger("01190700049996", 16).toByteArray()

    }

    var blist = arrayListOf<Int>()
    var pre_blist = arrayListOf<Int>()

    var pre_predata = 0.0f
    var predata = 0.0f

    private var dataSize = 0


    private var ecg_dataCnt = 0

    var connectionState = STATE_DISCONNECTED
    var bluetoothGatt: BluetoothGatt? = null
    var deviceAddress: String = ""
    private val bluetoothAdapter: BluetoothAdapter by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    inner class LocalBinder: Binder() {
        var service = this@BLEService_F400
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

                    blist.clear()
                    ecg_dataCnt = 0

                    val ecgData = it.value
                    if (ecgData[0].toInt() == 6 && ecgData[1].toInt() == 5){
                        measurementEcgValue(ecgData)
                    } else if (ecgData[0].toInt() == 6 && ecgData[1].toInt() == 3){
                        //최대/최소 심박
                        val maxb = (ecgData[4].toUByte() and UByte.MAX_VALUE).toByte()
                        val minb = (ecgData[5].toUByte() and UByte.MAX_VALUE).toByte()
                        DlogUtil.d("ddd", "최대최소")
                    }

                    //val intent = Intent(action)
                    intent.putExtra(EXTRA_ECG, blist)
                    sendBroadcast(intent)
                }
                else -> {}
            }
        } ?: sendBroadcast(intent)
    }

    fun measurementEcgValue(bArr: ByteArray) {
        val i: Int = (bArr[2].toUByte() and UByte.MAX_VALUE).toInt() + ((bArr[3].toUByte() and UByte.MAX_VALUE).toInt() shl 8) - 6
        if (i == bArr.size - 6 && i % 3 == 0) {
            val i2 = i / 3
            var i3 = 4
            for (i4 in 0 until i2) {
                val i5 = i3 + 2
                var i6: Int = (bArr[i3].toUByte() and UByte.MAX_VALUE).toInt() + ((bArr[i3 + 1].toUByte() and UByte.MAX_VALUE).toInt() shl 8) + ((bArr[i5].toUByte() and UByte.MAX_VALUE).toInt() shl 16)

                if (bArr[i5].toInt() and -128 != 0) {
                    i6 = i6 or -16777216
                }

//                val i6Value = Integer.valueOf(i6)
//                nativeList.add(i6Value)

                var makeHeartVal: Int = i6 / 40
                println("chong-----ecgData==$makeHeartVal")

                i3 += 3
                if (ecg_dataCnt % 3 == 0) {
                    makeHeartVal = ((makeHeartVal.toFloat() + pre_predata + predata) / 3.0f).toInt()
                    if (makeHeartVal > 1500) {
                        makeHeartVal = 1500
                    }
                    if (makeHeartVal < -1500) {
                        makeHeartVal = -1500
                    }

                    println("chong-----ecgData2==$makeHeartVal")
                    if (makeHeartVal == 1500 || makeHeartVal == -1500)
                        blist.add(0)
                    else
                        blist.add(Integer.valueOf(makeHeartVal))

                }
                pre_predata = predata
                predata = makeHeartVal.toFloat()
                ecg_dataCnt++
            }
        }

    }
}