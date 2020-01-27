package dev.weinsheimer.sportscalendar.di

/*
val networkModule = module {
    single { provideDefaultInterceptor() }
    single { provideOkhttpClient(get()) }
    single { provideRetrofit(get()) }
    single { provideApiService(get()) }
}

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
*/