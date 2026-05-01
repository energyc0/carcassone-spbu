package app.context

import app.entities.Color
import app.entities.MEEPLE_COUNT
import app.entities.Player
import app.utils.AreaCoordinate
import app.utils.TileCoordinate
import app.utils.Vec2
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.awt.geom.Area
import kotlin.test.assertNotNull

internal class GameStateTest {
    private lateinit var gameState: GameState
    private lateinit var players : Map<Color, Player>
    @BeforeEach
    fun setUp() {
        players = mapOf(Color.RED to Player(Color.RED, "player1"),
            Color.GREEN to Player(Color.GREEN, "player2"),
            Color.BLUE to Player(Color.BLUE, "player2"))

        gameState = GameState(players.values.toTypedArray())
    }

    private fun expectName(name : String, color : Color) : Boolean {
        return players[color]?.name == name
    }

    @Test
    @DisplayName("GameState score test")
    fun scoreTest() {
        var color = gameState.getPlayerColor()
        gameState.addPlayerScore(mapOf(color to 10))
        assert(gameState.getPlayerScore() == 10)

        gameState.nextTurn()
        assert(gameState.getPlayerScore() == 0)
        color = gameState.getPlayerColor()
        gameState.addPlayerScore(mapOf(color to 5))
        assert(gameState.getPlayerScore() == 5)

        gameState.nextTurn()
        assert(gameState.getPlayerScore() == 0)

        gameState.nextTurn()
        assert(gameState.getPlayerScore() == 10)
    }

    @Test
    @DisplayName("GameState change players turn test")
    fun playersTest() {
        for (i in 1..players.size*2) {
            assert(expectName(gameState.getPlayerName(), gameState.getPlayerColor()))
            gameState.nextTurn()
        }
    }

    @Test
    @DisplayName("GameState meeple test")
    fun meepleTest() {
        for (j in 1..players.size) {
            for (i in 1..MEEPLE_COUNT) {
                val meep = gameState.findPlayerMeeple()
                assertNotNull(meep)
                meep.setArea(TileCoordinate(Vec2(0,0), AreaCoordinate(0,0)))
            }
            assertNull(gameState.findPlayerMeeple())
            gameState.nextTurn()
        }
        assertNull(gameState.findPlayerMeeple())
    }
}