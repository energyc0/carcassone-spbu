package app.utils

import app.entities.Tile
import app.services.Direction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.awt.geom.Area

internal class TileCoordinateTest {
    private lateinit var v: TileCoordinate

    @BeforeEach
    fun setUp() {
        v = TileCoordinate(Vec2(1, 2), AreaCoordinate(TILE_AREA_SAMPLES - 1, 0))
    }

    @Test
    @DisplayName("Get adjacent coordinates test")
    fun adjacentCoordsTest() {
        val left = v.getAdjacent(Direction.LEFT)
        val leftExpected = TileCoordinate(v.tileCoord, AreaCoordinate(v.areaCoord.x - 1, 0))
        val right = v.getAdjacent(Direction.RIGHT)
        val rightExpected =
            TileCoordinate(
                Vec2(v.tileCoord.x + 1, v.tileCoord.y),
                AreaCoordinate(0, v.areaCoord.y),
            )
        val up = v.getAdjacent(Direction.UP)
        val upExpected =
            TileCoordinate(
                Vec2(v.tileCoord.x, v.tileCoord.y + 1),
                AreaCoordinate(v.areaCoord.x, TILE_AREA_SAMPLES - 1),
            )
        val down = v.getAdjacent(Direction.DOWN)
        val downExpected =
            TileCoordinate(
                v.tileCoord,
                AreaCoordinate(v.areaCoord.x, v.areaCoord.y + 1),
            )

        assert(right == rightExpected)
        assert(left == leftExpected)
        assert(up == upExpected)
        assert(down == downExpected)
    }

    @Test
    @DisplayName("TileCoordinate equiality test")
    fun equalityTest() {
        assert(v == TileCoordinate(Vec2(1, 2), AreaCoordinate(TILE_AREA_SAMPLES - 1, 0)))
        assert(v != TileCoordinate(Vec2(0, 2), AreaCoordinate(TILE_AREA_SAMPLES - 1, 0)))
        assert(v != TileCoordinate(Vec2(1, 2), AreaCoordinate(0, 0)))
    }
}
