package app

class GameBoard (private val gameRules: IGameRules){
    private val boardTiles = mutableMapOf<Coordinate, Tile>()

    fun insertTile(newTile: Tile, coordinate: Coordinate) {
        if (boardTiles.isEmpty()) {
            boardTiles[coordinate] = newTile
            return
        }
        val adjacentCoords = arrayOf<Coordinate>(
            Coordinate(coordinate.x, coordinate.y + 1),
            Coordinate(coordinate.x, coordinate.y - 1),
            Coordinate(coordinate.x + 1, coordinate.y),
            Coordinate(coordinate.x - 1, coordinate.y)
        )

        val adjacentDirs = arrayOf<Direction> (
            Direction.UP,
            Direction.DOWN,
            Direction.RIGHT,
            Direction.LEFT
        )

        if (boardTiles[coordinate] != null || adjacentCoords.fold(true){ acc, cord -> acc && (boardTiles[cord] == null) })
            throw IllegalArgumentException("Cannot insert the tile there.")
        for (i in 0..<4) {
            val cord = adjacentCoords[i]
            val toTile = boardTiles[cord] ?: continue
            val dir = adjacentDirs[i]
            if (!gameRules.canConnect(newTile, toTile, dir))
                throw IllegalArgumentException("Cannot insert the tile there.")
        }
    }

    fun getTile(coordinate: Coordinate) : Tile? {
        return boardTiles[coordinate]
    }
}