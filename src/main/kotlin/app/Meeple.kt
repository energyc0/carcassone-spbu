package app

enum class Color { RED, GREEN, BLUE, YELLOW, BLACK }

/*
 * Meeple is a game figure that belongs to a player.
 */
class Meeple(val color: Color) {
    fun isOnBoard() : Boolean {
        TODO("Need to implement a board.")
    }
    fun returnToPlayer() {
        TODO("Need to implement a board.")
    }
    fun setArea() {
        TODO("Need to implement a board.")
    }
}