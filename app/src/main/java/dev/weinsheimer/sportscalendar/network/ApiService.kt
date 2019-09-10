package dev.weinsheimer.sportscalendar.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

//private const val BASE_URL = "http://10.0.2.2:5000"
private const val BASE_URL = "http://88.153.223.200:5000"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val okhttp = OkHttpClient.Builder()
    .addInterceptor(MockInterceptor())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

private val retrofitMock = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(okhttp)
    .build()

interface ApiService {
    /**
     * ping pong
     */
    @GET("ping")
    suspend fun getPing(): Response<NetworkPingContainer>

    /**
     * Common
     */
    @GET("countries")
    suspend fun getCountries(): Response<NetworkCountryContainer>

    @GET("{sport}/athletes")
    suspend fun getAthletes(@Path("sport") sport: String): Response<NetworkBadmintonAthletesContainer>

    /**
     * Badminton
     */
    @GET("badminton/athletes")
    suspend fun getBadmintonAthletes(): Response<NetworkBadmintonAthletesContainer>

    @GET("badminton/events")
    suspend fun getBadmintonEvents(): Response<NetworkBadmintonEventsContainer>

    @GET("badminton/events/categories")
    suspend fun getBadmintonEventCategories(): Response<NetworkBadmintonEventCategoriesContainer>

    @Headers("Content-Type:application/json")
    @POST("badminton/events")
    suspend fun getBadmintonFilterResults(@Body body: NetworkFilterResultsRequest): Response<NetworkFilterResultsContainer>

    /**
     * Cycling
     */
    @GET("cycling/athletes")
    suspend fun getCyclingAthletes(): Response<NetworkCyclingAthletesContainer>

    @GET("cycling/events")
    suspend fun getCyclingEvents(): Response<NetworkCyclingEventsContainer>

    @GET("cycling/events/categories")
    suspend fun getCyclingEventCategories(): Response<NetworkCyclingEventCategoriesContainer>

    @Headers("Content-Type:application/json")
    @POST("cycling/events")
    suspend fun getCyclingFilterResults(@Body body: NetworkFilterResultsRequest): Response<NetworkFilterResultsContainer>

    /**
     * Tennis
     */
    @GET("tennis/athletes")
    suspend fun getTennisAthletes(): Response<NetworkTennisAthletesContainer>

    @GET("tennis/events")
    suspend fun getTennisEvents(): Response<NetworkTennisEventsContainer>

    @GET("tennis/events/categories")
    suspend fun getTennisEventCategories(): Response<NetworkTennisEventCategoriesContainer>

    @Headers("Content-Type:application/json")
    @POST("tennis/events")
    suspend fun getTennisFilterResults(@Body body: NetworkFilterResultsRequest): Response<NetworkFilterResultsContainer>
}

object Api {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
    val retrofitMockService: ApiService by lazy {
        retrofitMock.create(ApiService::class.java)
    }
}