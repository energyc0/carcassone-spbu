package app

enum class Color { RED, GREEN, BLUE, YELLOW, BLACK }

/*
 * Meeple is a game figure that belongs to a player.
 */
class Meeple(
    val color: Color,
) {
    private var coord : Coordinate? = null
    private var area : TileAreaDir? = null
    fun isOnBoard(): Boolean {
        return coord != null
    }

    fun returnToPlayer() {
        check(!isOnBoard()) {"Meeple is on board."}
        coord = null
        area = null
    }

    fun setArea(coords: Coordinate, tileArea: TileAreaDir) {
        coord = coords
        area = tileArea
    }
}
