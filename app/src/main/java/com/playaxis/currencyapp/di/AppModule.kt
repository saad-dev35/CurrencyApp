package com.playaxis.currencyapp.di

import android.app.Application
import android.content.Context
import com.playaxis.currencyapp.core.datastore.DatastoreManager
import com.playaxis.currencyapp.data.data_source.local.ExchangeRateDao
import com.playaxis.currencyapp.data.data_source.remote.CurrencyService
import com.playaxis.currencyapp.data.repositories.CurrencyRepository
import com.playaxis.currencyapp.worker.WorkerHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    fun provideContext(application: Application): Context = application.applicationContext

    /**---------------------------- Preference DataStore ----------------------------*/

    @Singleton
    @Provides
    fun providesDatastoreManager(context: Context): DatastoreManager {
        return DatastoreManager(context = context)
    }

    /**---------------------------- Note Repository ----------------------------*/

    @Singleton
    @Provides
    fun providesCurrencyRepository(
        service: CurrencyService,
        dao: ExchangeRateDao
    ): CurrencyRepository {
        return CurrencyRepository(service = service,dao = dao)
    }

    /**---------------------------- Worker Helper ----------------------------*/

    @Singleton
    @Provides
    fun providesWorkerHelper(context: Context): WorkerHelper {
        return WorkerHelper(context)
    }

}