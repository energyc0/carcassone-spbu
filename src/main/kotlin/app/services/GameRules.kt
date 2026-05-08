package app.services

import app.entities.GameObjectType
import app.entities.Tile

class GameRules : IGameRules {
    override fun canConnect(
        tile: Tile,
        to: Tile,
        from: Direction,
    ): Boolean = tile.getConnectionType(from.getOpposite()) == to.getConnectionType(from)

    override fun havePossibleConnections(
        tile: Tile,
        to: Tile,
        from: Direction,
    ): Boolean {
        val connections = Direction.entries.map { dir -> tile.getConnectionType(dir) }
        val fromType = to.getConnectionType(from)
        return connections.any { fromType == it }
    }
}
