package app.context

import app.Color
import app.Meeple
import app.Player
import kotlin.random.Random

/*
 * GameState class is responsible for choosing the next player's turn
 * and for player's operations like setting a Meeple and getting score.
 */
abstract class IGameState(
    private val players: Array<Player>,
) {
    private var curPlayerIndex: Int = Random.nextInt(0, players.size - 1)
    private var curPlayer: Player = players[curPlayerIndex]

    private fun findPlayer(color: Color): Player =
        players.find {
            it.color == color
        } ?: throw NoSuchElementException("There is no player with such color.")

    fun nextTurn() {
        curPlayerIndex = (curPlayerIndex + 1) % players.size
    }

    fun getPlayerScore(): Int = players[curPlayerIndex].score

    fun getPlayerName(): String = players[curPlayerIndex].name

    fun getPlayerColor(): Color = players[curPlayerIndex].color

    fun addPlayerScore(scores: Map<Color, Int>) {
        scores.forEach { i -> findPlayer(i.key).addScore(i.value) }
    }

    fun findPlayerMeeple(): Meeple? = curPlayer.findFreeMeeple()
}