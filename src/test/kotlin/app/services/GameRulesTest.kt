package app.services

import app.entities.GameObjectType
import app.entities.Rotation
import app.entities.Tile
import app.entities.TileLook
import app.utils.TILE_AREA_SAMPLES
import app.utils.TILE_AREA_SAMPLES_TOTAL
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class GameRulesTest {
    @Test
    @DisplayName("GameRules can tiles be connected")
    fun connectTest() {
        val gameRules = GameRules()
        val tileLookData1 = Array(TILE_AREA_SAMPLES_TOTAL, { GameObjectType.CITY })
        val tileLookData2 = Array(TILE_AREA_SAMPLES_TOTAL, { GameObjectType.FIELD })

        tileLookData1[TileLook.MID_SAMPLE] = GameObjectType.ROAD
        tileLookData1[TILE_AREA_SAMPLES_TOTAL - TILE_AREA_SAMPLES + TileLook.MID_SAMPLE] = GameObjectType.MONASTERY
        tileLookData2[TileLook.MID_SAMPLE] = GameObjectType.ROAD
        tileLookData2[TILE_AREA_SAMPLES_TOTAL - TILE_AREA_SAMPLES + TileLook.MID_SAMPLE] = GameObjectType.MONASTERY
        val tile1 = Tile(TileLook(tileLookData1))
        val tile2 = Tile(TileLook(tileLookData1))

        assert(!gameRules.canConnect(tile1, tile2, Direction.UP))
        assert(!gameRules.canConnect(tile1, tile2, Direction.DOWN))
        assert(gameRules.canConnect(tile1, tile2, Direction.LEFT))
        assert(gameRules.canConnect(tile1, tile2, Direction.RIGHT))

        tile1.setRotation(Rotation.FLIPPED)
        assert(gameRules.canConnect(tile1, tile2, Direction.UP))
        assert(gameRules.canConnect(tile1, tile2, Direction.DOWN))
        assert(gameRules.canConnect(tile1, tile2, Direction.LEFT))
        assert(gameRules.canConnect(tile1, tile2, Direction.RIGHT))
    }
}
