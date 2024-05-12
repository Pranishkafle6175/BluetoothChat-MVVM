package com.example.mvvmbluetooth.di

import android.content.Context
import com.example.mvvmbluetooth.data.BluetoothRespositoryImpl
import com.example.mvvmbluetooth.domain.BluetoothRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideBluetoothRepository(@ApplicationContext context: Context): BluetoothRepository {
        return BluetoothRespositoryImpl(context)
    }
}