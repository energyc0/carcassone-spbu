package app.services

import app.entities.GameObjectType
import app.entities.Tile
import app.utils.bottomCoordinates
import app.utils.leftCoordinates
import app.utils.rightCoordinates
import app.utils.topCoordinates

class GameRules : IGameRules {
    override fun canConnect(
        tile: Tile,
        to: Tile,
        from: Direction,
    ): Boolean = tile.getDirType(from.getOpposite()) == to.getDirType(from)

    private fun canBeConnected(
        edge1: Array<GameObjectType>,
        edge2: Array<GameObjectType>,
    ): Boolean = edge1 contentEquals edge2
}
