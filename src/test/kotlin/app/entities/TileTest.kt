package app.entities

import app.entities.TileLookTest.Companion.tileLookData1
import app.services.Direction
import app.utils.AreaCoordinate
import app.utils.TILE_AREA_SAMPLES
import app.utils.TILE_AREA_SAMPLES_TOTAL
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test

internal class TileTest {
    companion object {
        val tileLookData =
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
    }

    @Test
    @DisplayName("Tile initialization test")
    fun initTest() {
        assertDoesNotThrow {
            val tile1 = Tile(TileLook(tileLookData), true)
            val tile2 = Tile(TileLook(tileLookData))

            assert(tile1.coords == null && tile1.isStarting)
            assert(tile2.coords == null && !tile2.isStarting)
        }
    }

    @Test
    @DisplayName("Tile test TileLook rotated")
    fun rotatedLookTest() {
        val tile = Tile(TileLook(tileLookData, Rotation.LEFT))
        for (i in 0..<TILE_AREA_SAMPLES_TOTAL) {
            val coord =
                AreaCoordinate(
                    i / TILE_AREA_SAMPLES,
                    TILE_AREA_SAMPLES - (i % TILE_AREA_SAMPLES) - 1,
                )
            assert(tile.getTileArea(coord) == tileLookData1[i])
        }

        tile.setRotation(Rotation.STRAIGHT)
        for (i in 0..<TILE_AREA_SAMPLES_TOTAL) {
            val coord = AreaCoordinate(i % TILE_AREA_SAMPLES, i / TILE_AREA_SAMPLES)
            assert(tile.getTileArea(coord) == tileLookData1[i])
        }

        tile.setRotation(Rotation.RIGHT)
        for (i in 0..<TILE_AREA_SAMPLES_TOTAL) {
            val coord =
                AreaCoordinate(
                    TILE_AREA_SAMPLES - i / TILE_AREA_SAMPLES - 1,
                    TILE_AREA_SAMPLES - (i % TILE_AREA_SAMPLES) - 1,
                )
            assert(tile.getTileArea(coord) == tileLookData1[i])
        }

        tile.setRotation(Rotation.FLIPPED)
        for (i in 0..<TILE_AREA_SAMPLES_TOTAL) {
            val coord =
                AreaCoordinate(
                    TILE_AREA_SAMPLES - i % TILE_AREA_SAMPLES - 1,
                    TILE_AREA_SAMPLES - i / TILE_AREA_SAMPLES - 1,
                )
            val dataIdx = coord.x + coord.y * TILE_AREA_SAMPLES
            val tileCoord = AreaCoordinate(i % TILE_AREA_SAMPLES, i / TILE_AREA_SAMPLES)
            assert(tile.getTileArea(tileCoord) == tileLookData1[dataIdx])
        }
    }

    @Test
    @DisplayName("Tile get edges test")
    fun edgesTest() {
        val tile = Tile(TileLook(tileLookData, Rotation.STRAIGHT))
        assert(tile.getConnectionType(Direction.UP) == GameObjectType.CITY)
        assert(tile.getConnectionType(Direction.LEFT) == GameObjectType.FIELD)
        assert(tile.getConnectionType(Direction.RIGHT) == GameObjectType.FIELD)
        assert(tile.getConnectionType(Direction.DOWN) == GameObjectType.CROSSROAD)

        tile.setRotation(Rotation.LEFT)
        assert(tile.getConnectionType(Direction.UP) == GameObjectType.FIELD)
        assert(tile.getConnectionType(Direction.LEFT) == GameObjectType.CITY)
        assert(tile.getConnectionType(Direction.RIGHT) == GameObjectType.CROSSROAD)
        assert(tile.getConnectionType(Direction.DOWN) == GameObjectType.FIELD)
    }
}
