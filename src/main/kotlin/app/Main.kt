package app
import kotlin.random.Random
import kotlin.random.nextInt

fun main() {
    val gameContext = GameContext(PlayersInitializer(), GameTilesLoader(), GameRules())

    gameContext.gameplay()
}
