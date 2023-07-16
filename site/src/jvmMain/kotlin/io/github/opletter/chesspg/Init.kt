package io.github.opletter.chesspg

import com.varabyte.kobweb.api.data.add
import com.varabyte.kobweb.api.init.InitApi
import com.varabyte.kobweb.api.init.InitApiContext
import com.varabyte.kobweb.api.stream.StreamClientId
import io.github.opletter.chesspg.chessbackend.Game
import java.util.*

class ChessGame(
    val white: StreamClientId,
    val black: StreamClientId,
    val watchers: MutableSet<StreamClientId> = mutableSetOf(),
    val id: UUID = UUID.randomUUID(),
    var lastMove: Pair<String, String>? = null,
    val gameBackend: Game = Game.create()!!,
)

fun ChessGame.getRole(streamerId: StreamClientId) = when (streamerId) {
    white -> "white"
    black -> "black"
    else -> "spectator"
}

class Database {
    val lookingForGame = mutableSetOf<StreamClientId>()
    val games = mutableMapOf<UUID, ChessGame>()
    private val players = mutableMapOf<StreamClientId, UUID>()

    fun getGame(streamerId: StreamClientId) = games[players[streamerId]]

    fun registerGame(game: ChessGame) {
        games[game.id] = game
        players[game.white] = game.id
        players[game.black] = game.id
    }

    fun unregisterGame(game: ChessGame) {
        games.remove(game.id)
        players.remove(game.white)
        players.remove(game.black)
    }
}

@InitApi
fun initDatabase(ctx: InitApiContext) {
    ctx.data.add(Database())
}