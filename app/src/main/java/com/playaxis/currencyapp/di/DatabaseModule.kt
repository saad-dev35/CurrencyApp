package com.playaxis.currencyapp.di

import android.content.Context
import androidx.room.Room
import com.playaxis.currencyapp.data.data_source.local.RoomDB
import com.playaxis.currencyapp.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun providesRoomDb(appContext: Context): RoomDB {
        return Room.databaseBuilder(
            appContext,
            RoomDB::class.java,
            Constants.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    //DAO"S
    @Provides
    fun providesExchangeRateDao(roomDB: RoomDB) = roomDB.exchangeRateDao()

}