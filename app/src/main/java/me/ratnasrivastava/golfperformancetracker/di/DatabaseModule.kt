package me.ratnasrivastava.golfperformancetracker.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.ratnasrivastava.golfperformancetracker.data.local.GolfDatabase
import me.ratnasrivastava.golfperformancetracker.data.local.dao.PlayerDao
import me.ratnasrivastava.golfperformancetracker.data.local.dao.ShotDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideGolfDatabase(
        @ApplicationContext context: Context
    ): GolfDatabase =
        Room.databaseBuilder(
            context,
            GolfDatabase::class.java,
            GolfDatabase.DATABASE_NAME // "golf.db"
        )
            // For an assignment we use destructive migration so schema changes
            // don't crash during development. A production app would supply
            // proper Migration objects instead.
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun providePlayerDao(database: GolfDatabase): PlayerDao = database.playerDao()

    @Provides
    @Singleton
    fun provideShotDao(database: GolfDatabase): ShotDao = database.shotDao()
}