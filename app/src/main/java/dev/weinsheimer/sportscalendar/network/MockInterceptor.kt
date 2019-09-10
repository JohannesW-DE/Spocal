package dev.weinsheimer.sportscalendar.network

import com.google.gson.Gson
import com.squareup.moshi.Json
import dev.weinsheimer.sportscalendar.BuildConfig
import okhttp3.*
import timber.log.Timber
import com.google.gson.JsonObject



class MockInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (BuildConfig.DEBUG) {
            val uri = chain.request().url().uri().toString()
            val responseString = when {
                uri.endsWith("badminton/athletes") -> badmintonAthletes
                else -> ""
            }
            return chain.proceed(chain.request())
                .newBuilder()
                .code(200)
                .protocol(Protocol.HTTP_2)
                .message(responseString)
                .body(
                    ResponseBody.create(
                        MediaType.parse("application/json"),
                        responseString))
                .addHeader("Content-Type", "application/json")
                .build()
        } else {
            return chain.proceed(chain.request()).networkResponse()!!
        }
    }

}

const val badmintonAthletes = "{ \"athletes\": [ {\"gender\": \"m\", \"id\": 3, \"name\": \"Athlete #3\", \"nationality\": 1}, {\"gender\": \"m\", \"id\": 4, \"name\": \"Athlete #4\", \"nationality\": 1} ] }"
