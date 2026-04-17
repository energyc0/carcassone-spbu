package app

fun main() {
    val gameContext = GameContext(PlayersInitializer(), GameTilesLoader(), GameRules())

    gameContext.gameplay()
}
