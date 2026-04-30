package app.entities

import app.utils.AreaCoordinate
import app.utils.TileCoordinate
import app.utils.Vec2
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.awt.geom.Area

internal class MeepleTest {
    @Test
    @DisplayName("Meeple initialization test")
    fun initTest() {
        val m = Meeple(Color.GREEN)

        assert(!m.isOnBoard())
        assert(m.color == Color.GREEN)
        assertThrows(IllegalStateException::class.java) { m.returnToPlayer() }
    }

    @Test
    @DisplayName("Meeple set/return test")
    fun setRetTest() {
        val m = Meeple(Color.RED)

        for (i in 0..3) {
            assert(!m.isOnBoard())
            m.setArea(TileCoordinate(Vec2(i, i-1), AreaCoordinate(i+12, i-22)))
            assert(m.isOnBoard())
            assertDoesNotThrow { m.returnToPlayer() }
            assert(m.color == Color.RED)
        }
        assert(!m.isOnBoard())
    }
}