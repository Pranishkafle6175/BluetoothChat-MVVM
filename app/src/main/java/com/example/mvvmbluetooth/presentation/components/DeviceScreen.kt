package com.example.mvvmbluetooth.presentation.components

import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mvvmbluetooth.domain.BluetoothModule
import com.example.mvvmbluetooth.presentation.BluetoothUiState


@Composable
fun DeviceScreen(state:BluetoothUiState,
                 onStartScan:()-> Unit,
                 onStopScan:()-> Unit
){
    Column(modifier = Modifier
        .fillMaxSize()
    ) {
        Log.i(state.scannedDevices.toString(),"kdfkjhjkhkjd")

        DevicesList(
            scannedDevices = state.scannedDevices,
            pairedDevices = state.pairedDevices,
            modifer= Modifier
                .fillMaxWidth()
                .weight(1f),
            onClick={ }
            )

        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = onStartScan
            ) {
                Text(text = "StartScan")
            }

            Button(onClick = onStopScan
            ) {
                Text(text = "StopScan")
            }

        }
    }
}

@Composable
fun DevicesList(
    scannedDevices: List<BluetoothModule>,
    pairedDevices: List<BluetoothModule>,
    onClick:(BluetoothModule)->Unit,
    modifer: Modifier
) {
   LazyColumn(modifier = modifer){
       item {
           Text(text = "PairedDevices",
               fontSize = 24.sp,
               fontWeight = FontWeight.Bold,
               modifier = Modifier.padding(16.dp)
           )
       }

       items(pairedDevices){device->
           Text(
               text = device.name ?: "(No name)",
               modifier = Modifier.fillMaxWidth()
                   .padding(16.dp)
                   .clickable { onClick(device) }
           )

       }

       item {
           Text(text = "ScannedDevice",
               fontSize = 24.sp,
               fontWeight = FontWeight.Bold,
               modifier = Modifier.padding(16.dp)
           )
       }

       items(scannedDevices){device->
           Text(
               text = device.name ?: "No name",
               modifier = Modifier.fillMaxWidth()
                   .padding(16.dp)
                   .clickable { onClick(device) }
           )
           Log.i("jgsjhd","hsjkzhg")
           Log.i(device.name.toString(),device.name.toString());

       }


   }
}
