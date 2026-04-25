package app

fun main() {
    val gameRules = GameRules()
    val gameContext = GameContext(PlayersInitializer(), GameTilesLoader(), gameRules)

    gameContext.gameplay(TurnSuggester(gameRules), GUIManager(), PlayerController(), ScoreCounter())
}
