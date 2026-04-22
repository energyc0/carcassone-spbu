package app

class GameRules : IGameRules {
    override fun canConnect(
        tile: Tile,
        to: Tile,
        from: Direction,
    ): Boolean {
        // From left to right
        val topEdge = arrayOf<TileAreaDir>(TileAreaDir.CORNER_TOP_LEFT, TileAreaDir.TOP_LEFT, TileAreaDir.TOP, TileAreaDir.TOP_RIGHT, TileAreaDir.CORNER_TOP_RIGHT)
        // From top to bottom
        val leftEdge = arrayOf<TileAreaDir>(TileAreaDir.CORNER_TOP_LEFT, TileAreaDir.LEFT_TOP, TileAreaDir.LEFT, TileAreaDir.LEFT_BOTTOM, TileAreaDir.CORNER_BOTTOM_LEFT)
        // From top to bottom
        val rightEdge = arrayOf<TileAreaDir>(TileAreaDir.CORNER_TOP_RIGHT, TileAreaDir.RIGHT_TOP, TileAreaDir.RIGHT, TileAreaDir.RIGHT_BOTTOM, TileAreaDir.CORNER_BOTTOM_RIGHT)
        // From left to right
        val bottomEdge = arrayOf<TileAreaDir>(TileAreaDir.CORNER_BOTTOM_LEFT, TileAreaDir.BOTTOM_LEFT, TileAreaDir.BOTTOM, TileAreaDir.BOTTOM_RIGHT, TileAreaDir.CORNER_BOTTOM_RIGHT)

        val tileEdge = when(from) {
            Direction.RIGHT -> leftEdge
            Direction.LEFT -> rightEdge
            Direction.UP -> bottomEdge
            Direction.DOWN -> topEdge
        }
        val toTileEdge = when(from) {
            Direction.RIGHT -> rightEdge
            Direction.LEFT -> leftEdge
            Direction.UP -> topEdge
            Direction.DOWN -> bottomEdge
        }

        for ((i, dir) in tileEdge.withIndex()) {
            if (tile.getTileAreaType(dir) != to.getTileAreaType(toTileEdge[i]))
                return false
        }
        return true
    }

    private fun canBeConnected(edge1 : Array<TileAreaType>, edge2 : Array<TileAreaType>): Boolean {
        return edge1 contentEquals edge2
    }

}
