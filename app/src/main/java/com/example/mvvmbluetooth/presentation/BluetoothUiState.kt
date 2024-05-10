package com.example.mvvmbluetooth.presentation

import com.example.mvvmbluetooth.domain.BluetoothModule

data class BluetoothUiState(
    val scannedDevices : List<BluetoothModule> = emptyList(),
    val pairedDevices : List<BluetoothModule> = emptyList()
)
