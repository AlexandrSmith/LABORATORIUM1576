package dev.kodit.school1576.common

import io.ktor.http.cio.websocket.DefaultWebSocketSession
import java.util.concurrent.atomic.AtomicInteger

class Connection(val session: DefaultWebSocketSession, val projectId: Long) {
    companion object {
        var lastId = AtomicInteger(0)
    }
    val name = "${lastId.getAndIncrement()}"
}
