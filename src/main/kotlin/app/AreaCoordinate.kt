package app

const val TILE_AREA_SAMPLES_ROW = 5
const val TILE_AREA_SAMPLES_COLUMN = 5
const val TILE_AREA_SAMPLES_TOTAL = TILE_AREA_SAMPLES_COLUMN * TILE_AREA_SAMPLES_ROW

/*
 * TileArea's are indexed from top left to bottom right using AreaCoordinate class.
 * These coordinates are in the range modulo TILE_AREA_SAMPLES_ROW and TILE_AREA_SAMPLES_COLUMN.
 */

class AreaCoordinate (coordX : Int, coordY: Int) {
    val x = Math.floorMod(coordX, TILE_AREA_SAMPLES_ROW)
    val y = Math.floorMod(coordY,TILE_AREA_SAMPLES_COLUMN)

    fun getAdjacent(dir : Direction) : AreaCoordinate {
        return when (dir) {
            Direction.UP -> AreaCoordinate(x, y - 1)
            Direction.DOWN -> AreaCoordinate(x, y + 1)
            Direction.LEFT -> AreaCoordinate(x-1, y)
            Direction.RIGHT -> AreaCoordinate(x+1, y)
        }
    }
}


/* Top edge from left to right. */
val topCoordinates = Array<AreaCoordinate>(TILE_AREA_SAMPLES_ROW) { i -> AreaCoordinate(i, 0) }
/* Down edge from left to right. */
val bottomCoordinates = Array<AreaCoordinate>(TILE_AREA_SAMPLES_ROW) { i -> AreaCoordinate(i, TILE_AREA_SAMPLES_COLUMN-1) }
/* Left edge from top to bottom. */
val leftCoordinates = Array<AreaCoordinate>(TILE_AREA_SAMPLES_COLUMN) {  i -> AreaCoordinate(0, i)  }
/* Right edge from top to bottom. */
val rightCoordinates = Array<AreaCoordinate>(TILE_AREA_SAMPLES_COLUMN) {  i -> AreaCoordinate(TILE_AREA_SAMPLES_ROW-1, i)  }