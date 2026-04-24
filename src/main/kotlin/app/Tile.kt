package app

class Tile(
    val isStarting: Boolean,
    private val tileLook: TileLook,
) {
    var coords: Vec2? = null
        private set

    fun getTileArea(cord: AreaCoordinate): TileAreaType = tileLook.getArea(cord)

    fun setTile(coord: Vec2) {
        require(coords == null) { "Tile is already set. " }
        coords = coord
    }

    // up, left, down, right from 0 to 4 indices
    fun getTileEdges(): Array<Array<TileAreaType>> {
        val edges = arrayOf(topCoordinates, leftCoordinates, bottomCoordinates, rightCoordinates)
        return Array(edges.size) { edge -> Array(TILE_AREA_SAMPLES) { i -> getTileArea(edges[edge][i]) } }
    }
}
