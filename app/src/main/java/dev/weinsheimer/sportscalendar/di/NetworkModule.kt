package dev.weinsheimer.sportscalendar.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.weinsheimer.sportscalendar.BuildConfig
import dev.weinsheimer.sportscalendar.database.SpocalDB
import dev.weinsheimer.sportscalendar.network.ApiService
import dev.weinsheimer.sportscalendar.network.MockInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

val networkModule = module {
    single { provideDefaultInterceptor() }
    single { provideOkhttpClient(get()) }
    single { provideRetrofit(get()) }
    single { provideApiService(get()) }
}

/*
val networkTestModule = module(override = true) {
    single { provideDefaultInterceptor() }
    single { provideOkhttpClient(get()) }
    single { provideRetrofit(get()) }
    single { provideApiService(get()) }
}
*/

class DoNothingInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
}

fun provideDefaultInterceptor(): Interceptor {
    return DoNothingInterceptor()
}

fun provideOkhttpClient(interceptor: Interceptor): OkHttpClient {
    return OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Connection", "close")
                .addHeader("Transfer-Encoding", "chunked")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(interceptor)
        .build()
}

fun provideRetrofit(client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BuildConfig.SERVER_BASE_URL)
        .client(client)
        .addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder().add(KotlinJsonAdapterFactory()).build()))
        .build()
}

fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

const val DEFAULT_NAMESPACE = "default"