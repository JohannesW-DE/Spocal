package dev.weinsheimer.sportscalendar.network

import com.google.gson.Gson
import com.squareup.moshi.Json
import dev.weinsheimer.sportscalendar.BuildConfig
import okhttp3.*
import timber.log.Timber
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import android.R.id.message
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody


class MockInterceptor(val fake: (String) -> String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (BuildConfig.DEBUG) {
            println("INTERCEPTED")
            val uri = chain.request().url.toUri().toString()


            println(uri)
            val responseString = fake(uri)
            println(responseString)

            val mockRequest = Request.Builder()
                .url(BuildConfig.SERVER_BASE_URL)
                .build()

            return Response.Builder()
                .request(mockRequest)
                .protocol(Protocol.HTTP_2)
                .code(200)
                .message("")
                .body(
                    responseString
                        .toResponseBody("application/json".toMediaTypeOrNull())
                )
                .build()
        } else {
            return chain.proceed(chain.request())
        }
    }
}
