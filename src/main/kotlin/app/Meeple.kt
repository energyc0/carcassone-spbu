package app

enum class Color { RED, GREEN, BLUE, YELLOW, BLACK }

/*
 * Meeple is a game figure that belongs to a player.
 */
class Meeple(
    val color: Color,
) {
    private var coord: Vec2? = null
    private var area: Vec2? = null

    fun isOnBoard(): Boolean = coord != null

    fun returnToPlayer() {
        check(!isOnBoard()) { "Meeple is on board." }
        coord = null
        area = null
    }

    fun setArea(
        coords: Vec2,
        tileArea: Vec2,
    ) {
        coord = coords
        area = tileArea
    }
}
