package io.github.opletter.chesspg.api

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.setBodyText
import io.github.opletter.chesspg.Database
import io.github.opletter.chesspg.chessbackend.fen.FenSerializer

@Api
fun activeGames(ctx: ApiContext) {
    val database = ctx.data.getValue<Database>()
    val response = database.games.entries.joinToString(",") {
        it.key.toString() + ':' + FenSerializer.serialize(it.value.gameBackend.state).substringBefore(' ')
    }
    ctx.res.setBodyText(response)
}