package app.entities

import app.utils.AreaCoordinate
import app.utils.TileCoordinate
import app.utils.Vec2
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class PlayerTest {
    private lateinit var player : Player

    @BeforeEach
    fun setUp() {
        player = Player(Color.RED, "testing")
    }

    @Test
    @DisplayName("Player initialization test")
    fun initTest() {
        assert(player.score == 0)
        for (i in 1..MEEPLE_COUNT) {
            val m = player.findFreeMeeple()
            assertNotNull(m)
            m.setArea(TileCoordinate(Vec2(0,0), AreaCoordinate(0,0)))
        }
        assertNull(player.findFreeMeeple())
    }

    @Test
    @DisplayName("Player add score test")
    fun addScoreTest() {
        player.addScore(10)
        assert(player.score == 10)

        assertDoesNotThrow {
            player.addScore(0)
            assert(player.score == 10)
        }

        assertThrows(IllegalArgumentException::class.java) { player.addScore(-123) }
    }
}