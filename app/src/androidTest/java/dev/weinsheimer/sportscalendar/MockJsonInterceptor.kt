package dev.weinsheimer.sportscalendar

import android.content.Context
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.koin.core.KoinComponent
import org.koin.core.get

class MockJsonInterceptor : Interceptor, KoinComponent {
    override fun intercept(chain: Interceptor.Chain): Response {
        println("INTERCEPTOR")
        if (BuildConfig.DEBUG) {
            val context: Context = get()
            val method = chain.request().method
            var path = chain.request().url.toUri().path

            println("INTERCEPT")
            println(path)

            path = path.removePrefix("/").replace("/", "_")
            if (method == "POST") {
                path += "_post"
            }
            path += ".json"

            println(path)

            val jsonString = context.assets.open(path).bufferedReader().use{
                it.readText()
            }

            println(jsonString)

            val mockRequest = Request.Builder()
                .url(BuildConfig.SERVER_BASE_URL)
                .build()

            return Response.Builder()
                .request(mockRequest)
                .protocol(Protocol.HTTP_2)
                .code(200)
                .message("")
                .body(
                    jsonString
                        .toResponseBody("application/json".toMediaTypeOrNull())
                )
                .build()
        } else {
            return chain.proceed(chain.request())
        }
    }
}