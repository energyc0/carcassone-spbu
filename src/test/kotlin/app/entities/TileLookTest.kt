package app.entities

import app.entities.TileTest.Companion.tileLookData
import app.services.Direction
import app.utils.AreaCoordinate
import app.utils.TILE_AREA_SAMPLES
import app.utils.TILE_AREA_SAMPLES_TOTAL
import app.utils.TileCoordinate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.awt.geom.Area

internal class TileLookTest {
    companion object {
        val tileLookData1 = Array(TILE_AREA_SAMPLES_TOTAL) { i->
            when(i/5) {
                0 -> GameObjectType.CITY
                1 -> GameObjectType.ROAD
                2 -> GameObjectType.FIELD
                3 -> GameObjectType.MONASTERY
                4 -> GameObjectType.CROSSROAD
                else -> throw IllegalStateException("Failed testing TileLookTest: expected 5 types of GameObject.")
            }
        }

        val tileLookData2 = Array(TILE_AREA_SAMPLES_TOTAL) { i->
            val coord = AreaCoordinate(i % TILE_AREA_SAMPLES, i / TILE_AREA_SAMPLES)
            when(coord) {
                AreaCoordinate(0,0)-> GameObjectType.CITY
                AreaCoordinate(TILE_AREA_SAMPLES-1,0)-> GameObjectType.FIELD
                AreaCoordinate(0,TILE_AREA_SAMPLES-1)-> GameObjectType.MONASTERY
                AreaCoordinate(TILE_AREA_SAMPLES-1,TILE_AREA_SAMPLES-1)-> GameObjectType.ROAD
                else -> GameObjectType.CROSSROAD
            }
        }
    }

    @Test
    @DisplayName("TileLook initialization test")
    fun setUp() {
        assert(tileLookData1.size == TILE_AREA_SAMPLES_TOTAL)
        assertDoesNotThrow { val tileLook = TileLook(tileLookData1) }
    }

    @Test
    @DisplayName("TileLook rotation test")
    fun rotationTest() {
        val tileLook1 = TileLook(tileLookData1, Rotation.LEFT)

        assert(tileLook1.rotation == Rotation.LEFT)
        for(i in 0..<TILE_AREA_SAMPLES_TOTAL) {
            val coord = AreaCoordinate(i / TILE_AREA_SAMPLES, TILE_AREA_SAMPLES-(i % TILE_AREA_SAMPLES) - 1)
            assert(tileLook1.getArea(coord) == tileLookData1[i])
        }

        tileLook1.setRotation(Rotation.STRAIGHT)
        for(i in 0..<TILE_AREA_SAMPLES_TOTAL) {
            val coord = AreaCoordinate(i % TILE_AREA_SAMPLES, i / TILE_AREA_SAMPLES)
            assert(tileLook1.getArea(coord) == tileLookData1[i])
        }

        tileLook1.setRotation(Rotation.RIGHT)
        for(i in 0..<TILE_AREA_SAMPLES_TOTAL) {
            val coord = AreaCoordinate(TILE_AREA_SAMPLES - i / TILE_AREA_SAMPLES - 1, TILE_AREA_SAMPLES-(i % TILE_AREA_SAMPLES) - 1)
            assert(tileLook1.getArea(coord) == tileLookData1[i])
        }

        tileLook1.setRotation(Rotation.FLIPPED)
        for(i in 0..<TILE_AREA_SAMPLES_TOTAL) {
            val coord = AreaCoordinate(TILE_AREA_SAMPLES - i % TILE_AREA_SAMPLES - 1, TILE_AREA_SAMPLES - i / TILE_AREA_SAMPLES - 1)
            val dataIdx = coord.x + coord.y * TILE_AREA_SAMPLES
            val tileCoord = AreaCoordinate(i % TILE_AREA_SAMPLES, i / TILE_AREA_SAMPLES)
            assert(tileLook1.getArea(tileCoord) == tileLookData1[dataIdx])
        }

        val tileLook2 = TileLook(tileLookData2, Rotation.STRAIGHT)
        assert(tileLook2.getArea(AreaCoordinate(0,0)) == tileLookData2[0])
        tileLook2.setRotation(Rotation.RIGHT)
        assert(tileLook2.getArea(AreaCoordinate(0,0)) == tileLookData2[TILE_AREA_SAMPLES_TOTAL - TILE_AREA_SAMPLES])
        tileLook2.setRotation(Rotation.LEFT)
        assert(tileLook2.getArea(AreaCoordinate(0,0)) == tileLookData2[TILE_AREA_SAMPLES-1])
        tileLook2.setRotation(Rotation.FLIPPED)
        assert(tileLook2.getArea(AreaCoordinate(0,0)) == tileLookData2[TILE_AREA_SAMPLES_TOTAL-1])
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