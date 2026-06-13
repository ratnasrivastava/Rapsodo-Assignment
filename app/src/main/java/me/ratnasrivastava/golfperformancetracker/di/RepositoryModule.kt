package me.ratnasrivastava.golfperformancetracker.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.ratnasrivastava.golfperformancetracker.data.repository.GolfRepositoryImpl
import me.ratnasrivastava.golfperformancetracker.data.util.DefaultDispatcherProvider
import me.ratnasrivastava.golfperformancetracker.data.util.DispatcherProvider
import me.ratnasrivastava.golfperformancetracker.domain.repository.GolfRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGolfRepository(
        impl: GolfRepositoryImpl
    ): GolfRepository

    @Binds
    @Singleton
    abstract fun bindDispatcherProvider(
        impl: DefaultDispatcherProvider
    ): DispatcherProvider

    companion object {
        // TODO
    }
}