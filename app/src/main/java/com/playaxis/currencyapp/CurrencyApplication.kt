package com.playaxis.currencyapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.playaxis.currencyapp.utils.NetworkConnectivityService
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class CurrencyApplication : Application(), Configuration.Provider {

    //scope for coroutines
    private val backgroundScope = CoroutineScope(Dispatchers.IO)

    @Inject
    lateinit var workerFactory: HiltWorkerFactory


    override fun onCreate() {
        super.onCreate()

        backgroundScope.launch {
            //init internet service
            NetworkConnectivityService.instance.initializeWithApplicationContext(this@CurrencyApplication)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        backgroundScope.cancel()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setWorkerFactory(workerFactory).build()
    }

//    override val workManagerConfiguration: Configuration
//        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

}