package com.example.mvvmbluetooth.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.registerReceiver
import com.example.mvvmbluetooth.data.mapper.toBluetoothDevice
import com.example.mvvmbluetooth.domain.BluetoothModule
import com.example.mvvmbluetooth.domain.BluetoothRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.core.content.ContextCompat.registerReceiver
import com.example.mvvmbluetooth.domain.ConnectionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import java.io.IOException
import java.util.UUID


@SuppressLint("MissingPermission")
class BluetoothRespositoryImpl(
    private val context: Context
) :BluetoothRepository {

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


    private var bluetoothServerSocket: BluetoothServerSocket? =null
    private var bluetoothClientSocket:BluetoothSocket?=null




    init {
        updatePairedDevices()
    }

    private fun updatePairedDevices() {
        if(!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            Log.i("UpdatePairedDevice","Nopermission")
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

        if(!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            Log.i("StartDiscovery","Nopermission")

            return
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(foundeviceReceiver,filter)

        updatePairedDevices()

        bluetoothAdapter?.startDiscovery()



    }




    override fun stopdiscovery() {
        if(!hasPermission(Manifest.permission.BLUETOOTH_SCAN)){
            Log.i("Stop Discovery","Nopermission")

            return
        }
        bluetoothAdapter?.cancelDiscovery()
    }

    override fun startBluetoothServer(): Flow<ConnectionResult> {
        return flow{
            if(!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)){
                throw SecurityException("No Permission for the Bluetooth Connect")
            }

            bluetoothServerSocket=bluetoothAdapter?.
                listenUsingInsecureRfcommWithServiceRecord("chat_servie", UUID.fromString(UUID_NO))

            var shouldLoop= true
            while(shouldLoop){
                val bluetoothClientSocket :BluetoothSocket?=try {

                    bluetoothServerSocket?.accept()

                }catch (e:IOException){

                    shouldLoop=false
                    bluetoothServerSocket?.close()
                    null
                }
            }
            emit(ConnectionResult.ConnectionEstablished)
            bluetoothClientSocket?.let {
                bluetoothServerSocket?.close()
            }
        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    override fun connectToDevice(device: BluetoothModule): Flow<ConnectionResult> {

        return flow{
            bluetoothClientSocket=bluetoothAdapter?.getRemoteDevice(device.address)
                ?.createRfcommSocketToServiceRecord(UUID.fromString(UUID_NO))

            stopdiscovery()

            bluetoothClientSocket.let {socket->
                try {
                    socket?.connect()
                    emit(ConnectionResult.ConnectionEstablished)
                }catch (e:IOException){
                    socket?.close()
                    bluetoothClientSocket=null
                    emit(ConnectionResult.Error("Connection was Interrupted"))
                }

            }

        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    override fun closeConnection() {
        bluetoothServerSocket?.close()
        bluetoothClientSocket?.close()
        bluetoothServerSocket=null
        bluetoothClientSocket=null
    }

//    If you're unable to find the unregisterReceiver function in your class,
//    it's likely because you're trying to call it on an instance of a class that does not have this function defined.
//In Android, the unregisterReceiver function is typically called on a Context object,
// as it's responsible for managing the lifecycle of broadcast receivers.

    override fun release() {
        context.unregisterReceiver(foundeviceReceiver)
        closeConnection()
    }

    companion object{
        const val UUID_NO="ef37ab44-a09a-4f3c-a332-f184ad978250"
    }
}

