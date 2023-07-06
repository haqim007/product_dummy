package dev.haqim.productdummy.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.haqim.productdummy.BuildConfig
import dev.haqim.productdummy.data.local.LocalDataSource
import dev.haqim.productdummy.data.local.room.ProductDatabase
import dev.haqim.productdummy.data.remote.RemoteDataSource
import dev.haqim.productdummy.data.remote.mediator.ProductRemoteMediator
import dev.haqim.productdummy.data.remote.network.ApiService
import dev.haqim.productdummy.data.repository.ProductRepository
import dev.haqim.productdummy.domain.repository.IProductRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideProductRepository(
        productRepository: ProductRepository
    ): IProductRepository
    
}

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideProductRemoteMediator(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource
    ): ProductRemoteMediator {
        return ProductRemoteMediator(localDataSource, remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(
        database: ProductDatabase
    ): LocalDataSource {
        return LocalDataSource.getInstance(database)
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(
        service: ApiService
    ): RemoteDataSource {
        return RemoteDataSource(service)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) =
        Room
            .databaseBuilder(context, ProductDatabase::class.java, "product_dummy.db")
            .fallbackToDestructiveMigration().build()
    
    
}


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule{

    private var baseUrl: String = BuildConfig.BASE_URL
    fun setBaseUrl(url: String) {
        baseUrl = url
    }
    
    @Provides
    @Singleton
    fun provideOkHttp() = 
        OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)

    @Provides
    @Singleton
    fun providerRetrofit(
        okHttpClient: OkHttpClient.Builder
    ): Retrofit {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = if(BuildConfig.DEBUG){
            okHttpClient
                .addInterceptor(loggingInterceptor)
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build()
        }else{
            okHttpClient
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build()
        }

        return Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(
        retrofit: Retrofit
    ): ApiService {
        return retrofit.create(ApiService::class.java)
    }


}