package app

enum class Rotation { STRAIGHT, RIGHT, LEFT, FLIPPED }

/*
 * TileLook is a class that is responsible for
 * tile appearance in the game.
 */
class TileLook(
    private val areas: Array<TileAreaType>,
) {
    var rotation = Rotation.STRAIGHT
        private set

    init {
        require(areas.size == TILE_AREA_SAMPLES_TOTAL) { "Tile must contain ${TILE_AREA_SAMPLES_TOTAL} TileArea\'s" }
    }

    fun setRotation(rot: Rotation) {
        rotation = rot
    }

    fun getDrawData() {
        TODO("Need to implement GUI.")
    }

    fun getArea(cord: AreaCoordinate): TileAreaType {
        val curCord = rotateCord(cord)
        return areas[countCordIdx(curCord)]
    }

    private fun countCordIdx(cord: AreaCoordinate): Int = cord.y * TILE_AREA_SAMPLES + cord.x

    private fun rotateCord(cord: AreaCoordinate): AreaCoordinate {
        val maxX = TILE_AREA_SAMPLES - 1
        val maxY = TILE_AREA_SAMPLES - 1
        return when (rotation) {
            Rotation.STRAIGHT -> cord
            Rotation.FLIPPED -> AreaCoordinate(maxX - cord.x, maxY - cord.y)
            Rotation.LEFT -> AreaCoordinate(cord.y, maxY - cord.x)
            Rotation.RIGHT -> AreaCoordinate(maxX - cord.y, cord.x)
        }
    }
}
