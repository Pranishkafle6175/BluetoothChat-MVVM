package com.example.mvvmbluetooth.domain

sealed interface ConnectionResult {
    object ConnectionEstablished:ConnectionResult
    data class Error(val message:String):ConnectionResult

}