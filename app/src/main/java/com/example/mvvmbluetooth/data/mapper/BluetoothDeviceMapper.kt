package com.example.mvvmbluetooth.data.mapper

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.example.mvvmbluetooth.domain.BluetoothModule


// I have to us BluetoothDevice.to........   here because in the calling cuntion we pass the objects sent by bondeddevices in
//BluetoothAdapter which is of type BluetoothDevice so In extension function
//i need to use theexact type to changing type function

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDevice():BluetoothModule{
    return BluetoothModule(
        name=name,
        address=address
    )
}