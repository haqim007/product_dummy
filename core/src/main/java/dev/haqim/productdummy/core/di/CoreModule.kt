package dev.haqim.productdummy.core.di

import androidx.room.Room
import dev.haqim.productdummy.core.BuildConfig
import dev.haqim.productdummy.core.data.local.LocalDataSource
import dev.haqim.productdummy.core.data.local.room.ProductDatabase
import dev.haqim.productdummy.core.data.remote.RemoteDataSource
import dev.haqim.productdummy.core.data.remote.mediator.ProductRemoteMediator
import dev.haqim.productdummy.core.data.remote.network.ApiService
import dev.haqim.productdummy.core.data.repository.ProductRepository
import dev.haqim.productdummy.core.domain.repository.IProductRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            ProductDatabase::class.java, "product_dummy.db"
        ).fallbackToDestructiveMigration().build()
    }
}

val networkModule = module {
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
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
