package app.services

import app.entities.Player

const val MAX_PLAYERS_COUNT = 5

interface IPlayersInitializer {
    // Return empty array of players or at least 2-players array.
    fun collectPlayers(): Array<Player>
}
