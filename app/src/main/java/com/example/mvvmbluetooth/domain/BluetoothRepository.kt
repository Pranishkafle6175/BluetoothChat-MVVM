package com.example.mvvmbluetooth.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothRepository {

    val scannedDevices: StateFlow<List<BluetoothModule>>
    val pairedDevices: StateFlow<List<BluetoothModule>>

    fun startdiscovery()
    fun stopdiscovery()

    fun startBluetoothServer():Flow<ConnectionResult>
    fun connectToDevice(device:BluetoothModule):Flow<ConnectionResult>

    fun closeConnection()
    fun release()

}