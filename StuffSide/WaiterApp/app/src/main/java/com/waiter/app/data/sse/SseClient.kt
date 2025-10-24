package com.waiter.app.data.sse

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import com.waiter.app.core.AppConfig
import java.io.BufferedReader
import java.io.InputStreamReader

object SseClient {
    private val http = OkHttpClient()

    fun eventsFlow() = callbackFlow<String> {
        val req = Request.Builder().url(AppConfig.SSE_URL).build()
        val call = http.newCall(req)

        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                call.execute().use { resp ->
                    if (!resp.isSuccessful || resp.body == null) {
                        close(IllegalStateException("SSE response not ok"))
                        return@use
                    }
                    BufferedReader(InputStreamReader(resp.body!!.byteStream())).use { br ->
                        var event: String? = null
                        var line: String?
                        while (true) {
                            line = br.readLine() ?: break
                            if (line.startsWith("event: ")) event = line.substring(7).trim()
                            if (line.startsWith("data: ")) {
                                val json = line.substring(6).trim()
                                trySend(json)
                                event = null
                            }
                        }
                    }
                }
            } catch (t: Throwable) {
                close(t)
            }
        }

        awaitClose {
            try { call.cancel() } catch (_: Throwable) {}
            job.cancel()
        }
    }
}
