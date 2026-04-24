package app

class GameRules : IGameRules {
    override fun canConnect(
        tile: Tile,
        to: Tile,
        from: Direction,
    ): Boolean {
        val tileEdge =
            when (from) {
                Direction.RIGHT -> leftCoordinates
                Direction.LEFT -> rightCoordinates
                Direction.UP -> bottomCoordinates
                Direction.DOWN -> topCoordinates
            }
        val toTileEdge =
            when (from) {
                Direction.RIGHT -> rightCoordinates
                Direction.LEFT -> leftCoordinates
                Direction.UP -> topCoordinates
                Direction.DOWN -> bottomCoordinates
            }

        for ((i, dir) in tileEdge.withIndex()) {
            if (tile.getTileArea(dir).type != to.getTileArea(toTileEdge[i]).type) {
                return false
            }
        }
        return true
    }

    private fun canBeConnected(
        edge1: Array<TileAreaType>,
        edge2: Array<TileAreaType>,
    ): Boolean = edge1 contentEquals edge2
}
