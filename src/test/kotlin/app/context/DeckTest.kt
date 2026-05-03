package app.context

import app.entities.GameObjectType
import app.entities.Tile
import app.entities.TileLook
import app.services.GameTilesLoader
import app.utils.TILE_AREA_SAMPLES_TOTAL
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class DeckTest {
    @Test
    @DisplayName("Deck initialization test")
    fun initTest() {
        assertDoesNotThrow {
            val tiles = GameTilesLoader().loadTiles()
            assert(tiles.size == TILES_DECK_COUNT)
            val deck = Deck(tiles)
            assert(deck.hasNextTile())
        }

        assertThrows(IllegalArgumentException::class.java) { val deck = Deck(mutableListOf()) }
        assertThrows(IllegalArgumentException::class.java) {
            val tile = Tile(TileLook(Array(TILE_AREA_SAMPLES_TOTAL) { GameObjectType.CITY }))
            val deck = Deck(MutableList(TILES_DECK_COUNT - 2, { tile }))
        }
    }

    @Test
    @DisplayName("Deck get tiles test")
    fun getTilesTest() {
        assertDoesNotThrow {
            val tiles = GameTilesLoader().loadTiles()
            assert(tiles.size == TILES_DECK_COUNT)
            val deck = Deck(tiles)
            assert(deck.hasNextTile())

            assertDoesNotThrow {
                val tilesCount = TILES_DECK_COUNT / 2
                val gottenTiles = Array(tilesCount) { deck.getNextTile() }
                assert(deck.hasNextTile())
                for (i in 1..TILES_DECK_COUNT - tilesCount) {
                    deck.getNextTile()
                }
                assert(!deck.hasNextTile())
                gottenTiles.forEach { i -> deck.tileSetBack(i) }
                assert(deck.hasNextTile())
                for (i in 1..gottenTiles.size) {
                    deck.getNextTile()
                }
                assert(!deck.hasNextTile())
                assertThrows(NoSuchElementException::class.java) { deck.getNextTile() }
            }
        }
    }
}
