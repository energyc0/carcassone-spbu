package app

const val MEEPLE_COUNT = 8

class Player(
    val color: Color,
    val name: String,
) {
    private val meeple: Array<Meeple> = Array(MEEPLE_COUNT) { Meeple(color) }

    fun findFreeMeeple(): Meeple? = meeple.find { !it.isOnBoard() }

    var score: Int = 0
        private set

    fun addScore(sc: Int) {
        score += sc
    }
}
