package app

enum class TileAreaType { FIELD, CITY, MONASTERY, ROAD, CROSSROAD }

/*
 * TileArea is an abstract part of a whole tile.
 * It is used to constitute an object in the game,
 * to set game figures there and to count game scores.
 */
abstract class TileArea (val type: TileAreaType, areaScore: Int) : TileAreaScore(areaScore){
    private var meeple: Meeple? = null

    fun setMeeple(m: Meeple) {
        if (meeple != null)
            throw IllegalStateException("There is already a meeple here.")
        meeple = m
    }

    fun unsetMeeple(m: Meeple) {
        meeple = null
    }

    fun hasMeeple() : Boolean {
        return meeple != null
    }
}