package dev.weinsheimer.sportscalendar

import android.content.Context
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.koin.core.KoinComponent
import org.koin.core.get
import java.io.FileNotFoundException

class TestDispatcher : Dispatcher(), KoinComponent {
    val context: Context = get()

    @Throws(InterruptedException::class)
    override fun dispatch(request: RecordedRequest): MockResponse {
        when (request.method) {
            "GET" -> {
                request.path?.let { path ->
                    val fileName = "api".plus(path.replace("/", "_")).plus(".json")
                    return try {
                        val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
                        MockResponse().setBody(json) // response code 200 by default
                    } catch (e : FileNotFoundException) {
                        MockResponse().setResponseCode(404)
                    }
                }
            }
            "POST" -> {
                request.path?.let { path ->
                    val fileName = "api".plus(path.replace("/", "_")).plus("_post.json")
                    return try {
                        val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
                        MockResponse().setBody(json) // response code 200 by default
                    } catch (e : FileNotFoundException) {
                        MockResponse().setResponseCode(404)
                    }
                }
            }
        }
        return MockResponse().setResponseCode(404)
    }
}