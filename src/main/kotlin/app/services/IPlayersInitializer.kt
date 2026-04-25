package app.services

import app.entities.Player

interface IPlayersInitializer {
    fun collectPlayers(): Array<Player>
}
