package app

enum class Rotation { STRAIGHT, RIGHT, LEFT, FLIPPED }

// TileArea indices to access elements of TileLook
enum class TileAreaDir { CORNER_TOP_LEFT, TOP_LEFT, TOP, TOP_RIGHT, CORNER_TOP_RIGHT,
                        LEFT_TOP,                   CENTER_TOP,                 RIGHT_TOP,
                        LEFT,       CENTER_LEFT, CENTER,    CENTER_RIGHT,       RIGHT,
                        LEFT_BOTTOM,                CENTER_BOTTOM,              RIGHT_BOTTOM,
                        CORNER_BOTTOM_LEFT, BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT, CORNER_BOTTOM_RIGHT }

/*
 * TileLook is a class that is responsible for
 * tile appearance in the game.
 */
class TileLook (private val areas: Array<TileArea>){
    var rotation = Rotation.STRAIGHT
        private set

    init {
        require(areas.size == TileAreaDir.entries.size) {"Tile must contain ${TileAreaDir.entries.size} TileArea\'s"}
    }
    fun setRotation(rot: Rotation) {
        rotation = rot
    }
    fun setMeeple(m: Meeple, tileAreaDir: TileAreaDir) {
        val dir = rotDir(tileAreaDir)
        areas[dir.ordinal].setMeeple(m)
    }

    fun getDrawData() {
        TODO("Need to implement GUI.")
    }

    private fun rotDir(dir: TileAreaDir) : TileAreaDir{
        TODO("Rotate map.")
        return when (rotation) {
            Rotation.STRAIGHT -> dir
            Rotation.RIGHT -> dir
            Rotation.LEFT -> dir
            Rotation.FLIPPED -> dir
        }
    }
}