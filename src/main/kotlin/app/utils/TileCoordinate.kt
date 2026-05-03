package app.utils

import app.services.Direction

/*
 * TileCoordinate is a class that contains two coordinates: GameBoard coordinate and AreaCoordinate.
 * TileArea's are indexed from top left to bottom right using AreaCoordinate class.
 */

data class TileCoordinate(
    val tileCoord: Vec2,
    val areaCoord: AreaCoordinate,
) {
    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is TileCoordinate && other.tileCoord == this.tileCoord && other.areaCoord == this.areaCoord)

    fun getAdjacent(dir: Direction): TileCoordinate {
        val adjArea = areaCoord.getAdjacent(dir)
        return when (dir) {
            Direction.UP -> {
                if (adjArea.y == TILE_AREA_SAMPLES - 1) {
                    TileCoordinate(Vec2(tileCoord.x, tileCoord.y + 1), adjArea)
                } else {
                    copy(areaCoord = adjArea)
                }
            }

            Direction.DOWN -> {
                if (adjArea.y == 0) {
                    TileCoordinate(Vec2(tileCoord.x, tileCoord.y - 1), adjArea)
                } else {
                    copy(areaCoord = adjArea)
                }
            }

            Direction.LEFT -> {
                if (adjArea.x == TILE_AREA_SAMPLES - 1) {
                    TileCoordinate(Vec2(tileCoord.x - 1, tileCoord.y), adjArea)
                } else {
                    copy(areaCoord = adjArea)
                }
            }

            Direction.RIGHT -> {
                if (adjArea.x == 0) {
                    TileCoordinate(
                        Vec2(tileCoord.x + 1, tileCoord.y),
                        adjArea,
                    )
                } else {
                    copy(areaCoord = adjArea)
                }
            }
        }
    }

    fun getAdjacent(): Array<TileCoordinate> = Direction.entries.map { dir -> getAdjacent(dir) }.toTypedArray()

    override fun hashCode(): Int {
        var result = tileCoord.hashCode()
        result = 31 * result + areaCoord.hashCode()
        return result
    }
}
