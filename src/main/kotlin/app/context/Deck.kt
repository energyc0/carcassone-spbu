package app.context
import app.entities.Tile
import kotlin.random.Random

const val TILES_DECK_COUNT = 72

/**
 * There must be TILES_DECK_COUNT tiles in 'tiles' initialization.
 * Throws IllegalArgumentException on error.
 */
class Deck(
    tiles: MutableList<Tile>,
) {
    private val shuffledTiles = mutableListOf<Tile>()

    init {
        require(tiles.size == TILES_DECK_COUNT) { "There are must be $TILES_DECK_COUNT in the deck." }
        val startIndices = mutableListOf<Int>()
        for (i in 0..<TILES_DECK_COUNT) {
            if (tiles[i].isStarting) {
                startIndices.add(i)
            }
        }
        require(startIndices.isNotEmpty()) { "There must be at least one starting tile in the deck." }

        val startIndex = Random.nextInt(0, startIndices.size - 1)
        shuffledTiles.add(tiles[startIndex])
        tiles.removeAt(startIndex)
        shuffledTiles.addAll(tiles.shuffled())
    }

    /**
     * Get next tile or throw NoSuchElementException.
     */
    fun getNextTile(): Tile = shuffledTiles.removeLast()

    fun hasNextTile(): Boolean = shuffledTiles.isNotEmpty()

    fun tileSetBack(tile: Tile) {
        shuffledTiles.addFirst(tile)
    }
}
