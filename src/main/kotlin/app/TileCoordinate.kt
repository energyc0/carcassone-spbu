package app

/*
 * TileCoordinate is a class that contains two coordinates: GameBoard coordinate and AreaCoordinate.
 * TileArea's are indexed from top left to bottom right using AreaCoordinate class.
 */

data class TileCoordinate(val tCoord : Vec2, val areaCoord : AreaCoordinate) {
    fun getAdjacent(dir : Direction) : TileCoordinate {
        val adjArea = areaCoord.getAdjacent(dir)
        return when (dir) {
            Direction.UP -> if(adjArea.y == 4) TileCoordinate(Vec2(tCoord.x, tCoord.y + 1), adjArea) else copy(areaCoord = adjArea)
            Direction.DOWN -> if(adjArea.y == 0) TileCoordinate(Vec2(tCoord.x, tCoord.y - 1), adjArea) else copy(areaCoord = adjArea)
            Direction.LEFT -> if(adjArea.x == 4) TileCoordinate(Vec2(tCoord.x-1, tCoord.y), adjArea) else copy(areaCoord = adjArea)
            Direction.RIGHT -> if(adjArea.x == 4) TileCoordinate(Vec2(tCoord.x + 1, tCoord.y), adjArea) else copy(areaCoord = adjArea)
        }
    }
}