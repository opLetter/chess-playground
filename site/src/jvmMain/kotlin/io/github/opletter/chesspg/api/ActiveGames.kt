package io.github.opletter.chesspg.api

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.setBodyText
import io.github.opletter.chesspg.Database

@Api
fun activeGames(ctx: ApiContext) {
    val database = ctx.data.getValue<Database>()
    ctx.res.setBodyText(database.games.keys.joinToString(","))
}