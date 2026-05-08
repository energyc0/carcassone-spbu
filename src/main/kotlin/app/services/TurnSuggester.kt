package app.services

import app.context.IGameBoardRead
import app.entities.GameObjectType
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

    override fun suggestTurn(
        tile: Tile,
        board: IGameBoardRead,
    ): List<Vec2> {
        val space = board.getFreeSpace()
        // Edge case. Starting tile has no adjacent tiles.
        if (space.size == 1 && space[0] == Vec2(0, 0)) {
            return space
        }

        val connections = Direction.entries.map { dir -> tile.getConnectionType(dir) }
        val suggest = mutableListOf<Vec2>()

        space.forEach { freeCoord ->
            val hasValidConnection =
                freeCoord.getAdjacent().any { tileCoord ->
                    val toTile = board.getTile(tileCoord)
                    toTile != null &&
                        gameRules.havePossibleConnections(
                            tile,
                            toTile,
                            tileCoord.getDirectionTo(freeCoord),
                        )
                }
            if (hasValidConnection) {
                suggest.add(freeCoord)
            }
        }

        return suggest.toList()
    }
}
