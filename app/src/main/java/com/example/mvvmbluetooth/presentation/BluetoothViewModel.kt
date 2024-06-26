package com.example.mvvmbluetooth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmbluetooth.domain.BluetoothRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothrepository: BluetoothRepository
) :ViewModel(){

    private val _state = MutableStateFlow(BluetoothUiState())
    val state = combine(
        bluetoothrepository.scannedDevices,
        bluetoothrepository.pairedDevices,
        _state
    ){scannedDevices,pairedDevices,state ->
        state.copy(
            scannedDevices=scannedDevices,
            pairedDevices=pairedDevices
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),_state.value)

    fun startScan(){
        bluetoothrepository.startdiscovery()
    }
    fun stopScan(){
        bluetoothrepository.stopdiscovery()
    }
}