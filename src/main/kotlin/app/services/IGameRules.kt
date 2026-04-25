package app.services

import app.entities.Tile

enum class Direction {
    UP,
    LEFT,
    DOWN,
    RIGHT,
}

interface IGameRules {
    fun canConnect(
        tile: Tile,
        to: Tile,
        from: Direction,
    ): Boolean
}
