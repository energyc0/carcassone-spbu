package app

const val PLAYER_COUNT = 5

class GameState (private val players: Array<Player>) : IGameState(players) {
    init {
        require(players.size in 1..PLAYER_COUNT)
    }
}