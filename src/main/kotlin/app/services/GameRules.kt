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
            if (tile.getTileArea(dir) != to.getTileArea(toTileEdge[i])) {
                return false
            }
        }
        return true
    }

    private fun canBeConnected(
        edge1: Array<GameObjectType>,
        edge2: Array<GameObjectType>,
    ): Boolean = edge1 contentEquals edge2
}
