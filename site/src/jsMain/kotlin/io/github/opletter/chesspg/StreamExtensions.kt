package io.github.opletter.chesspg

import com.varabyte.kobweb.streams.ApiStream
import io.github.opletter.chesspg.models.ChessStreamEvent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun ApiStream.send(value: ChessStreamEvent.ClientSide) {
    send(Json.encodeToString(value))
}