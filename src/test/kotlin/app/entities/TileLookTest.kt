package app.entities

import app.services.Direction
import app.utils.AreaCoordinate
import app.utils.TILE_AREA_SAMPLES
import app.utils.TILE_AREA_SAMPLES_TOTAL
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class TileLookTest {
    companion object {
        val tileLookData1 =
            Array(TILE_AREA_SAMPLES_TOTAL) { i ->
                when (i / 5) {
                    0 -> GameObjectType.CITY
                    1 -> GameObjectType.ROAD
                    2 -> GameObjectType.FIELD
                    3 -> GameObjectType.MONASTERY
                    4 -> GameObjectType.CROSSROAD
                    else -> throw IllegalStateException("Failed testing TileLookTest: expected 5 types of GameObject.")
                }
            }

        val tileLookData2 =
            Array(TILE_AREA_SAMPLES_TOTAL) { i ->
                val coord = AreaCoordinate(i % TILE_AREA_SAMPLES, i / TILE_AREA_SAMPLES)
                when (coord) {
                    AreaCoordinate(0, 0) -> GameObjectType.CITY
                    AreaCoordinate(TILE_AREA_SAMPLES - 1, 0) -> GameObjectType.FIELD
                    AreaCoordinate(0, TILE_AREA_SAMPLES - 1) -> GameObjectType.MONASTERY
                    AreaCoordinate(TILE_AREA_SAMPLES - 1, TILE_AREA_SAMPLES - 1) -> GameObjectType.ROAD
                    else -> GameObjectType.CROSSROAD
                }
            }
    }

    @Test
    @DisplayName("TileLook initialization test")
    fun setUp() {
        assertEquals(TILE_AREA_SAMPLES_TOTAL, tileLookData1.size)
        assertDoesNotThrow { val tileLook = TileLook(tileLookData1) }
    }

    @Test
    @DisplayName("TileLook rotation test1")
    fun rotationTest1() {
        val tileLook1 = TileLook(tileLookData1, Rotation.LEFT)

        assert(tileLook1.rotation == Rotation.LEFT)
        for (i in 0..<TILE_AREA_SAMPLES_TOTAL) {
            val coord =
                AreaCoordinate(
                    i / TILE_AREA_SAMPLES,
                    TILE_AREA_SAMPLES - (i % TILE_AREA_SAMPLES) - 1,
                )
            assertEquals(tileLookData1[i], tileLook1.getArea(coord))
        }

        tileLook1.setRotation(Rotation.STRAIGHT)
        for (i in 0..<TILE_AREA_SAMPLES_TOTAL) {
            val coord =
                AreaCoordinate(
                    i % TILE_AREA_SAMPLES,
                    i / TILE_AREA_SAMPLES,
                )
            assertEquals(tileLookData1[i], tileLook1.getArea(coord))
        }

        tileLook1.setRotation(Rotation.RIGHT)
        for (i in 0..<TILE_AREA_SAMPLES_TOTAL) {
            val coord =
                AreaCoordinate(
                    TILE_AREA_SAMPLES - i / TILE_AREA_SAMPLES - 1,
                    TILE_AREA_SAMPLES - (i % TILE_AREA_SAMPLES) - 1,
                )
            assertEquals(tileLookData1[i], tileLook1.getArea(coord))
        }

        tileLook1.setRotation(Rotation.FLIPPED)
        for (i in 0..<TILE_AREA_SAMPLES_TOTAL) {
            val coord =
                AreaCoordinate(
                    TILE_AREA_SAMPLES - i % TILE_AREA_SAMPLES - 1,
                    TILE_AREA_SAMPLES - i / TILE_AREA_SAMPLES - 1,
                )
            val dataIdx = coord.x + coord.y * TILE_AREA_SAMPLES
            val tileCoord = AreaCoordinate(i % TILE_AREA_SAMPLES, i / TILE_AREA_SAMPLES)
            assertEquals(tileLookData1[dataIdx], tileLook1.getArea(tileCoord))
        }
    }

    @Test
    @DisplayName("TileLook rotation test2")
    fun rotationTest2() {
        val tileLook2 = TileLook(tileLookData2, Rotation.STRAIGHT)
        assertEquals(
            tileLookData2[0],
            tileLook2.getArea(AreaCoordinate(0, 0)),
        )
        tileLook2.setRotation(Rotation.RIGHT)
        assertEquals(
            tileLookData2[TILE_AREA_SAMPLES_TOTAL - TILE_AREA_SAMPLES],
            tileLook2.getArea(AreaCoordinate(0, 0)),
        )
        tileLook2.setRotation(Rotation.LEFT)
        assertEquals(
            tileLookData2[TILE_AREA_SAMPLES - 1],
            tileLook2.getArea(AreaCoordinate(0, 0)),
        )
        tileLook2.setRotation(Rotation.FLIPPED)
        assertEquals(
            tileLookData2[TILE_AREA_SAMPLES_TOTAL - 1],
            tileLook2.getArea(AreaCoordinate(0, 0)),
        )
    }

    @Test
    @DisplayName("TileLook getDirType() test")
    fun getDirTypeTest() {
        val tileLook = TileLook(tileLookData1)
        assert(tileLook.getDirType(Direction.UP) == GameObjectType.CITY)
        assert(tileLook.getDirType(Direction.LEFT) == GameObjectType.FIELD)
        assert(tileLook.getDirType(Direction.RIGHT) == GameObjectType.FIELD)
        assert(tileLook.getDirType(Direction.DOWN) == GameObjectType.CROSSROAD)

        tileLook.setRotation(Rotation.LEFT)
        assert(tileLook.getDirType(Direction.UP) == GameObjectType.FIELD)
        assert(tileLook.getDirType(Direction.LEFT) == GameObjectType.CITY)
        assert(tileLook.getDirType(Direction.RIGHT) == GameObjectType.CROSSROAD)
        assert(tileLook.getDirType(Direction.DOWN) == GameObjectType.FIELD)
    }
}
