package dev.haqim.productdummy

import android.app.Application
import dev.haqim.productdummy.core.di.databaseModule
import dev.haqim.productdummy.core.di.networkModule
import dev.haqim.productdummy.core.di.repositoryModule
import dev.haqim.productdummy.di.useCaseModule
import dev.haqim.productdummy.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@MyApplication)
            modules(
                listOf(
                    databaseModule,
                    networkModule,
                    repositoryModule,
                    useCaseModule,
                    viewModelModule
                )
            )
        }
    }
}