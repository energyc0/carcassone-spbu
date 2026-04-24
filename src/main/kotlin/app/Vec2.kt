package app

data class Vec2 (
    val x: Int,
    val y: Int,
) {
    fun getAdjacent() : Array<Vec2>  = arrayOf(
        copy(y = y + 1),
        copy(y = y - 1),
        copy(x = x + 1),
        copy(x = x - 1)
    )

}