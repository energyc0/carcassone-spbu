package app.services

import app.context.IGameBoardReadTile
import app.context.IGameBoardReadTileSpace
import app.entities.Tile
import app.utils.Vec2

class TurnSuggester(
    rules: IGameRules,
) : ITurnSuggester(rules) {
    private fun canBeConnected(
        tile: Tile,
        to: Tile,
        from: Direction,
    ): Boolean {
        val toEdge = to.getTileEdges()[from.ordinal]
        val tileEdges = tile.getTileEdges()

        return tileEdges.any { it contentEquals toEdge }
    }

    private fun hasPossibleConnection(
        tile: Tile,
        cord: Vec2,
        board: IGameBoardReadTile,
    ): Boolean {
        val adjCords = cord.getAdjacent()
        val adjTiles = mutableListOf<Tile>()

        adjCords.forEach {
            val temp = board.getTile(it)
            if (temp != null) adjTiles.add(temp)
        }
        if (adjTiles.isEmpty()) {
            throw IllegalStateException("There must be at least one adjacent tile.")
        }

        return adjTiles.any {
            val toCords = it.coords ?: throw IllegalStateException("Tile in the board must have coordinates.")
            val dir = cord.getDirection(toCords)
            canBeConnected(tile, it, dir)
        }
    }

    override fun suggestTurn(
        tile: Tile,
        board: IGameBoardReadTileSpace,
    ): List<Vec2> {
        val space = board.getFreeSpace()
        /* Edge case. Starting tile has no adjacent tiles. */
        if (space.size == 1 && space[0] == Vec2(0,0))
            return space

        val suggest = mutableListOf<Vec2>()

        space.forEach { if (hasPossibleConnection(tile, it, board)) suggest.add(it) }

        return suggest.toList()
    }
}
