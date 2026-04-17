package app

/*
 * GameState class is responsible for choosing the next player's turn
 * and for player's operations like setting a Meeple and getting score.
 */
abstract class IGameState (private val players: Array<Player>){
    private var curPlayerIndex: Int = 0

    private fun findPlayer(color: Color): Player {
        return players.find { it.color == color } ?: throw NoSuchElementException("There is no player with such color.")
    }

    fun nextTurn() {
        curPlayerIndex = (curPlayerIndex + 1) % players.size
    }

    fun getPlayerScore(): Int {
        return players[curPlayerIndex].score
    }

    fun getPlayerName(): String {
        return players[curPlayerIndex].name
    }

    fun getPlayerColor(): Color {
        return players[curPlayerIndex].color
    }

    fun addPlayerScore(color: Color, score: Int) {
        findPlayer(color).addScore(score)
    }

    fun setPlayerMeeple() {
        TODO("Need to implement a board.")
    }

    fun unsetPlayerMeeple(color: Color) {
        TODO("Need to implement a board.")
    }
}