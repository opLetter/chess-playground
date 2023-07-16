package io.github.opletter.chesspg

import com.varabyte.kobweb.api.stream.DisconnectedStream
import com.varabyte.kobweb.api.stream.Stream
import com.varabyte.kobweb.api.stream.StreamClientId
import com.varabyte.kobweb.api.stream.broadcast
import io.github.opletter.chesspg.models.ChessStreamEvent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

suspend inline fun Stream.send(value: ChessStreamEvent.ServerSide) {
    send(Json.encodeToString(value))
}

//suspend fun DisconnectedStream.broadcast(text: String, clientIds: Iterable<StreamClientId>)

suspend inline fun DisconnectedStream.broadcast(
    value: ChessStreamEvent.ServerSide,
    clientIds: Iterable<StreamClientId>,
) {
    broadcast(Json.encodeToString(value), clientIds)
}