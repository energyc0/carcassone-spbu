package app

const val TILE_AREA_SAMPLES = 5
const val TILE_AREA_SAMPLES_TOTAL = TILE_AREA_SAMPLES * TILE_AREA_SAMPLES

/*
 * TileArea's are indexed from top left to bottom right using AreaCoordinate class.
 * These coordinates are in the range modulo TILE_AREA_SAMPLES_ROW and TILE_AREA_SAMPLES_COLUMN.
 */

class AreaCoordinate(
    coordX: Int,
    coordY: Int,
) {
    val x = Math.floorMod(coordX, TILE_AREA_SAMPLES)
    val y = Math.floorMod(coordY, TILE_AREA_SAMPLES)

    fun getAdjacent(dir: Direction): AreaCoordinate =
        when (dir) {
            Direction.UP -> AreaCoordinate(x, y - 1)
            Direction.DOWN -> AreaCoordinate(x, y + 1)
            Direction.LEFT -> AreaCoordinate(x - 1, y)
            Direction.RIGHT -> AreaCoordinate(x + 1, y)
        }
}

// Top edge from left to right.
val topCoordinates = Array<AreaCoordinate>(TILE_AREA_SAMPLES) { i -> AreaCoordinate(i, 0) }

// Down edge from left to right.
val bottomCoordinates = Array<AreaCoordinate>(TILE_AREA_SAMPLES) { i -> AreaCoordinate(i, TILE_AREA_SAMPLES - 1) }

// Left edge from top to bottom.
val leftCoordinates = Array<AreaCoordinate>(TILE_AREA_SAMPLES) { i -> AreaCoordinate(0, i) }

// Right edge from top to bottom.
val rightCoordinates = Array<AreaCoordinate>(TILE_AREA_SAMPLES) { i -> AreaCoordinate(TILE_AREA_SAMPLES - 1, i) }
