package dev.weinsheimer.sportscalendar.network

import dev.weinsheimer.sportscalendar.BuildConfig
import okhttp3.*


class MockInterceptor(val fake: (String) -> String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (BuildConfig.DEBUG) {
            println("INTERCEPTED")
            val uri = chain.request().url().uri().toString()


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
                    ResponseBody.create(
                        MediaType.parse("application/json"),
                        responseString
                    )
                )
                .build()
        } else {
            return chain.proceed(chain.request())
        }
    }
}
