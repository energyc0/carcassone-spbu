package app.utils

import app.services.Direction
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.awt.geom.Area

internal class AreaCoordinateTest {
    @Test
    @DisplayName("AreaCoordinate equality test")
    fun equalityTest() {
        val a1 = AreaCoordinate(0,0)
        val a2 = AreaCoordinate(1,0)
        val a3 = AreaCoordinate(0,1)
        val a4 = AreaCoordinate(0,0)

        assert(a1 == a1)
        assert(a2 != a1)
        assert(a2 != a3)
        assert(a1 == a4)
        assert(a3 != a4)
        assert(a4 != a2)
    }

    @Test
    @DisplayName("AreaCoordinate initialization test")
    fun initTest() {
        val a1 = AreaCoordinate(0,1)
        val a2 = AreaCoordinate(TILE_AREA_SAMPLES-1, TILE_AREA_SAMPLES-2)
        val a3 = AreaCoordinate(TILE_AREA_SAMPLES, TILE_AREA_SAMPLES+1)
        val a4 = AreaCoordinate(-1, -2)

        assert(a1 == AreaCoordinate(0, 1))
        assert(a2 == AreaCoordinate(TILE_AREA_SAMPLES-1, TILE_AREA_SAMPLES-2))
        assert(a3 == AreaCoordinate(0, 1))
        assert(a4 == AreaCoordinate(TILE_AREA_SAMPLES-1, TILE_AREA_SAMPLES-2))
    }

    @Test
    @DisplayName("AreaCoordinate get adjacent coordinates test")
    fun getAdjacentTest() {
        val a = AreaCoordinate(TILE_AREA_SAMPLES-1, TILE_AREA_SAMPLES)
        val left = a.getAdjacent(Direction.LEFT)
        val right = a.getAdjacent(Direction.RIGHT)
        val up = a.getAdjacent(Direction.UP)
        val down = a.getAdjacent(Direction.DOWN)

        assert(left.x == a.x - 1 && left.y == a.y)
        assert(right.x == 0 && right.y == a.y)
        assert(up.y == TILE_AREA_SAMPLES-1 && up.x == a.x)
        assert(down.y == 1 && down.x == a.x)
    }
}