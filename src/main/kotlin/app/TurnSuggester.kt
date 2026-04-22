package app

class TurnSuggester(rules: IGameRules) : ITurnSuggester(rules) {

    /* To properly use this function coordinates must be adjacent */
    private fun calcAdjacentDirection(from: Coordinate, to: Coordinate) : Direction {
        val dx = to.x - from.x
        if (dx == 0) {
            val dy = to.y - from.y
            return if (dy < 0)
                Direction.DOWN
            else
                Direction.UP
        }
        return if (dx < 0)
            Direction.LEFT
        else
            Direction.RIGHT
    }

    private fun canBeConnected(tile: Tile, to: Tile, from: Direction) : Boolean{
        val toEdge = to.getTileEdges()[from.ordinal]
        val tileEdges = tile.getTileEdges()

        return tileEdges.any { it contentEquals toEdge}
    }

    private fun hasPossibleConnection(tile: Tile, cord: Coordinate, board: GameBoard) : Boolean {
        val adjCords = cord.getAdjacent()
        val adjTiles = mutableListOf<Tile>()

        adjCords.forEach {
            val temp = board.getTile(it)
            if(temp != null) adjTiles.add(temp)
        }
        if (adjTiles.isEmpty())
            throw IllegalStateException("There must be at least one adjacent tile.")

        return adjTiles.any {
            val toCords = it.coords ?: throw IllegalStateException("Tile in the board must have coordinates.")
            val dir = calcAdjacentDirection(cord, toCords)
            canBeConnected(tile, it, dir)
        }
    }

    override fun suggestTurn(tile: Tile, board: GameBoard) : List<Coordinate> {
        val space = board.getFreeSpace()

        val suggest = mutableListOf<Coordinate>()

        space.forEach { if (hasPossibleConnection(tile, it, board)) suggest.add(it) }

        return suggest.toList()
    }
}