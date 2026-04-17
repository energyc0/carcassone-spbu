package app

enum class Direction {UP, RIGHT, LEFT, DOWN}

interface IGameRules {
    fun canConnect(tile: Tile, to: Tile, from: Direction) : Boolean
}