package app.services

import app.context.TILES_DECK_COUNT
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class GameTilesLoaderTest {
    @Test
    @DisplayName("GameTilesLoaderTest test load from JSON resources")
    fun loadTest() {
        assertDoesNotThrow {
            val loader = GameTilesLoader()
            val tiles = loader.loadTiles()
            assert(tiles.size == TILES_DECK_COUNT)
        }
    }
}