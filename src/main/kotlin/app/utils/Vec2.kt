package app.utils

import app.services.Direction

data class Vec2(
    val x: Int,
    val y: Int,
) {
    fun getAdjacent(): Array<Vec2> =
        arrayOf(
            copy(y = y + 1),
            copy(y = y - 1),
            copy(x = x + 1),
            copy(x = x - 1),
        )

    /*
     * It is only usable for adjacent coordinates.
     */
    fun getDirection(to: Vec2): Direction {
        val dx = to.x - x
        if (dx == 0) {
            val dy = to.y - y
            return if (dy < 0) {
                Direction.DOWN
            } else {
                Direction.UP
            }
        }
        return if (dx < 0) {
            Direction.LEFT
        } else {
            Direction.RIGHT
        }
    }
}
