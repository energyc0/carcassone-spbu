package app.services

import app.entities.Tile

enum class Direction {
    UP,
    LEFT,
    DOWN,
    RIGHT,
    ;

    fun getOpposite(): Direction =
        when (this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }
}

interface IGameRules {
    fun canConnect(
        tile: Tile,
        to: Tile,
        from: Direction,
    ): Boolean
}
