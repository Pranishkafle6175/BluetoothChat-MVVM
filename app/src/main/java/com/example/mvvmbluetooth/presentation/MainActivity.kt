package com.example.mvvmbluetooth.presentation

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mvvmbluetooth.presentation.components.DeviceScreen
import com.example.mvvmbluetooth.ui.theme.MVVMBluetoothTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }
    private val isBluetoothEnabled : Boolean
        get() = bluetoothAdapter?.isEnabled==true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("Bluetooth",isBluetoothEnabled.toString());


        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { /* Not needed */ }

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            val canEnableBluetooth = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                perms[Manifest.permission.BLUETOOTH_CONNECT] == true
            } else true

            if(canEnableBluetooth && !isBluetoothEnabled) {
                enableBluetoothLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }
        }
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
            )
        )

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            permissionLauncher.launch(
//                arrayOf(
//                    Manifest.permission.BLUETOOTH_SCAN,
//                    Manifest.permission.BLUETOOTH_CONNECT,
//                    Manifest.permission.BLUETOOTH_ADVERTISE,
//                )
//            )
//        }

        setContent {
            MVVMBluetoothTheme {

                val viewmodel = hiltViewModel<BluetoothViewModel>()
                val state by viewmodel.state.collectAsState()


//                val enableBluetoothLauncher = rememberLauncherForActivityResult(
//                    contract = ActivityResultContracts.StartActivityForResult()
//                ){
//
//                }
//
//                val permissionLauncher= rememberLauncherForActivityResult(
//                    ActivityResultContracts.RequestMultiplePermissions()
//                ){ perms->
//                    val canEnableBluetooth = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                        perms[android.Manifest.permission.BLUETOOTH_CONNECT] == true
//                    } else true
//
//                    if(canEnableBluetooth && !isBluetoothEnabled ){
//                        enableBluetoothLauncher.launch(
//                            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//                        )
//                    }
//                }
//
//                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    permissionLauncher.launch(
//                        arrayOf(
//                            android.Manifest.permission.BLUETOOTH_SCAN,
//                            android.Manifest.permission.BLUETOOTH_CONNECT,
//                        )
//                    )
//                }




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

