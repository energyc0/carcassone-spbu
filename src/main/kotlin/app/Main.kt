package app

import app.context.GameContext
import app.gui.GUIManager
import app.services.GameRules
import app.services.GameTilesLoader
import app.services.PlayerController
import app.services.PlayersInitializer
import app.services.ScoreCounter
import app.services.TurnSuggester

fun main() {
    val gameRules = GameRules()
    val gameContext = GameContext(PlayersInitializer(), GameTilesLoader(), gameRules)

    gameContext.gameplay(TurnSuggester(gameRules), GUIManager(), PlayerController(), ScoreCounter())
}
