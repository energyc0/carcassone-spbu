package app.entities

import app.utils.TileCoordinate

enum class Color { RED, GREEN, BLUE, YELLOW, BLACK }

/*
 * Meeple is a game figure that belongs to a player.
 */
class Meeple(
    val color: Color,
) {
    private var coord: TileCoordinate? = null

    fun isOnBoard(): Boolean = coord != null

    // Check whether meep is on board and throw exception if he is or set coordinate to null
    fun returnToPlayer() {
        check(isOnBoard()) { "Meeple is not on board." }
        coord = null
    }

    fun setArea(coords: TileCoordinate) {
        coord = coords
    }
}
