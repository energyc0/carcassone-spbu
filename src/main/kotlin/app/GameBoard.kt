package app

class GameBoard(
    private val gameRules: IGameRules,
) {
    private val boardTiles = mutableMapOf<Coordinate, Tile>()
    private val freeSpace = FreeSpace()

    fun insertTile(
        newTile: Tile,
        coordinate: Coordinate,
    ) {
        if (!freeSpace.hasSpace(coordinate))
            throw IllegalArgumentException("Cannot insert the tile there.")
        /* Edge case */
        if (freeSpace.hasSpace(Coordinate(0,0))) {
            boardTiles[coordinate] = newTile
            freeSpace.takeSpace(coordinate)
            return
        }

        val adjacentDirs =
            arrayOf<Direction>(
                Direction.UP,
                Direction.DOWN,
                Direction.RIGHT,
                Direction.LEFT,
            )

        val adjacentCoords = coordinate.getAdjacent()
        if (boardTiles[coordinate] != null ||
            adjacentCoords.all { freeSpace.hasSpace(it) }
        ) {
            throw IllegalArgumentException("Cannot insert the tile there.")
        }

        for ((i, dir) in adjacentDirs.withIndex()) {
            val cord = adjacentCoords[i]
            val toTile = boardTiles[cord] ?: continue
            val dir = dir
            if (!gameRules.canConnect(newTile, toTile, dir)) {
                throw IllegalArgumentException("Cannot insert the tile there.")
            }
        }
        boardTiles[coordinate] = newTile
        freeSpace.takeSpace(coordinate)
    }

    fun getTile(coordinate: Coordinate): Tile? = boardTiles[coordinate]
}
