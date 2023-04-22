package dev.haqim.productdummy.core.di

import androidx.room.Room
import dev.haqim.productdummy.core.BuildConfig
import dev.haqim.productdummy.core.data.local.LocalDataSource
import dev.haqim.productdummy.core.data.local.room.ProductDatabase
import dev.haqim.productdummy.core.data.remote.RemoteDataSource
import dev.haqim.productdummy.core.data.remotemediator.ProductRemoteMediator
import dev.haqim.productdummy.core.data.remote.network.ApiService
import dev.haqim.productdummy.core.data.repository.ProductRepository
import dev.haqim.productdummy.core.domain.repository.IProductRepository
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val databaseModule = module {

    single {

        val passphrase: ByteArray = SQLiteDatabase.getBytes("product dummy".toCharArray())
        val factory = SupportFactory(passphrase)
        
        Room.databaseBuilder(
            androidContext(),
            ProductDatabase::class.java, "product_dummy.db"
        ).fallbackToDestructiveMigration()
            .openHelperFactory(factory)
            .build()
    }
}

val networkModule = module {
    single {
        val hostname = "dummyjson.com"
        val certificatePinner = CertificatePinner.Builder()
            .add(hostname, "sha256/EIUBK9vng6IOm1uzvmroX6a7JUf3zAzQRigruKm6T44=")
            .add(hostname, "sha256/jQJTbIh0grw0/1TkHSumWb+Fs0Ggogr621gT3PvPKG0=")
            .add(hostname, "sha256/C5+lpZ7tcVwmwQIMcRtPbsQtWLABXhQzejna0wHFr8M=")
            .build()
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .certificatePinner(certificatePinner)
            .build()
    }
    single {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
        retrofit.create(ApiService::class.java)
    }
}

val repositoryModule = module {
    single { LocalDataSource(get()) }
    single { RemoteDataSource(get()) }
    factory { ProductRemoteMediator(get(), get()) }
    single<IProductRepository> { ProductRepository(get(), get()) }
}
