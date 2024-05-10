package com.example.mvvmbluetooth.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.registerReceiver
import com.example.mvvmbluetooth.Manifest
import com.example.mvvmbluetooth.data.mapper.toBluetoothDevice
import com.example.mvvmbluetooth.domain.BluetoothModule
import com.example.mvvmbluetooth.domain.BluetoothRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.core.content.ContextCompat.registerReceiver



@SuppressLint("MissingPermission")
class BluetoothRespositoryImpl(
    private val context: Context
) :BluetoothRepository {

    companion object {
        private const val REQUEST_ENABLE_BT = 1
    }

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

 //Having two verisons MutableStateFLow and Stateflow Mutable can be changed Stateflow cannot be
//here using in this file Mutable one will be used as value need to be changed as processing but when the scennedDevices and
//    pairedDevices acessed by other classes data must not be modified so we provude Stateflow which is immutable
//    By exposing these lists as immutable StateFlow, the class ensures that external classes can only observe changes
//    , maintaining encapsulation and preventing unintended modifications to the state.

    private val _scannedDevices = MutableStateFlow<List<BluetoothModule>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothModule>>
        get() = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothModule>>(emptyList())
    override val pairedDevices: StateFlow<List<BluetoothModule>>
//    asStateflow converts immuntable verison to mutable version
        get() = _pairedDevices.asStateFlow()

    private val foundeviceReceiver= BluetoothReceiver{device->
        _scannedDevices.update {devices->
            val newdevice= device.toBluetoothDevice()
            if(newdevice in devices) devices else devices + newdevice
        }

    }



    init {
        updatePairedDevices()
    }

    private fun updatePairedDevices() {
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        // first it maps i.e for every object the particular operation is to be performed here every bluetooth device contains
//        name and adress differently so we convert that into single bluetoothobject  and  here also represent after mapping we want to perform
//        particular operation as well here we update _pairedDevices

        bluetoothAdapter
            ?.bondedDevices
            ?.map { it.toBluetoothDevice() }
            ?.also { devices ->
                _pairedDevices.update { devices }
            }

    }

    private fun hasPermission(permission: String): Boolean {

        return context.checkSelfPermission(permission)== PackageManager.PERMISSION_GRANTED

    }

// 0 in flags means there is no specific flags being set

    override fun startdiscovery() {

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(context,foundeviceReceiver,filter,0)

        updatePairedDevices()

    if(hasPermission(android.Manifest.permission.BLUETOOTH_SCAN)){
        bluetoothAdapter?.startDiscovery()
    }


    }

    override fun stopdiscovery() {
        if(hasPermission(android.Manifest.permission.BLUETOOTH_SCAN)){
            bluetoothAdapter?.cancelDiscovery()
        }
    }

//    If you're unable to find the unregisterReceiver function in your class,
//    it's likely because you're trying to call it on an instance of a class that does not have this function defined.
//In Android, the unregisterReceiver function is typically called on a Context object,
// as it's responsible for managing the lifecycle of broadcast receivers.

    override fun release() {
        context.unregisterReceiver(foundeviceReceiver)
    }
}

