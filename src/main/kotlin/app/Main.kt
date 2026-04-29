package app

import app.context.GameContext
import app.gui.GUIManager
import app.services.GameRules
import app.services.GameTilesLoader
import app.services.PlayerController
import app.services.PlayersInitializer
import app.services.ScoreCounter
import app.services.TurnSuggester
import kotlin.system.exitProcess

fun main() {
    val gameRules = GameRules()
    val players = PlayersInitializer().collectPlayers()
    if (players.isEmpty())
        return
    try {
        val gameContext = GameContext(players, GameTilesLoader(), gameRules)

        gameContext.gameplay(TurnSuggester(gameRules), GUIManager(), PlayerController(), ScoreCounter())
    } catch (e : IllegalStateException) {
        println("${e.message}")
        exitProcess(1)
    }
}
