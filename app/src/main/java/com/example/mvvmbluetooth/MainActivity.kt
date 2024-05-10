package com.example.mvvmbluetooth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mvvmbluetooth.presentation.BluetoothViewModel
import com.example.mvvmbluetooth.presentation.components.DeviceScreen
import com.example.mvvmbluetooth.ui.theme.MVVMBluetoothTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MVVMBluetoothTheme {

                val viewmodel = hiltViewModel<BluetoothViewModel>()
                val state by viewmodel.statee.collectAsState()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    DeviceScreen(
                        state=state,
                        onStartScan= viewmodel::startScan,
                        onStopScan=viewmodel::stopScan
                    )

                }
            }
        }
    }
}

