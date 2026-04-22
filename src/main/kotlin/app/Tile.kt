package app

data class Coordinate(
    val x: Int,
    val y: Int,
) {
    fun getAdjacent() : Array<Coordinate>  = arrayOf(
            Coordinate(x, y + 1),
            Coordinate(x, y - 1),
            Coordinate(x + 1, y),
            Coordinate(x - 1, y),
        )
}

class Tile(
    val isStarting: Boolean,
    private val tileLook: TileLook,
) {
    var coords: Coordinate? = null
        private set

    fun getTileArea(dir: TileAreaDir) : TileArea {
        return tileLook.getArea(dir)
    }

    fun setTile(coord: Coordinate) {
        require(coords == null) { "Tile is already set. " }
        coords = coord
    }

    fun hasMeeple(tileAreaDir: TileAreaDir,) : Boolean {
        return tileLook.getArea(tileAreaDir).hasMeeple()
    }

    fun setMeeple(
        meeple: Meeple,
        tileAreaDir: TileAreaDir,
    ) {
        tileLook.setMeeple(meeple, tileAreaDir)
    }

    /* up, left, down, right from 0 to 3 indices */
    fun getTileEdges() : Array<Array<TileAreaType>> {
        /* From left to right, from up to down */
        val up = arrayOf<TileAreaDir> (TileAreaDir.CORNER_TOP_LEFT, TileAreaDir.TOP_LEFT, TileAreaDir.TOP, TileAreaDir.TOP_RIGHT, TileAreaDir.CORNER_TOP_RIGHT,)
        val down = arrayOf<TileAreaDir> (TileAreaDir.CORNER_BOTTOM_LEFT, TileAreaDir.BOTTOM_LEFT, TileAreaDir.BOTTOM, TileAreaDir.BOTTOM_RIGHT, TileAreaDir.CORNER_BOTTOM_RIGHT,)
        val left = arrayOf<TileAreaDir> (TileAreaDir.CORNER_TOP_LEFT, TileAreaDir.LEFT_TOP, TileAreaDir.LEFT, TileAreaDir.LEFT_BOTTOM, TileAreaDir.CORNER_BOTTOM_LEFT,)
        val right = arrayOf<TileAreaDir> (TileAreaDir.CORNER_TOP_RIGHT, TileAreaDir.RIGHT_TOP, TileAreaDir.RIGHT, TileAreaDir.RIGHT_BOTTOM, TileAreaDir.CORNER_BOTTOM_RIGHT,)

        val edges = arrayOf(up, left, down, right)
        return Array(4) { edge -> Array(5) { i -> getTileArea(edges[edge][i]).type } }
    }
}
