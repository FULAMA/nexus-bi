package com.example.nexusbi

import android.app.Application
import com.example.nexusbi.data.local.CarnetDatabase
import com.example.nexusbi.data.remote.RemoteConfigManager
import com.example.nexusbi.data.repository.CarnetRepository

class CarnetApplication : Application() {
    val database by lazy { CarnetDatabase.getDatabase(this) }
    val repository by lazy { 
        CarnetRepository(
            customerDao = database.customerDao(),
            debtRecordDao = database.debtRecordDao(),
            groupOrderDao = database.groupOrderDao()
        ) 
    }

    override fun onCreate() {
        super.onCreate()
        RemoteConfigManager.initialize()
    }
}

