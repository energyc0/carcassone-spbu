package app

import java.util.Deque
import kotlin.collections.setOf

class ScoreCounter : IScoreCounter {
    private fun getAdjacentCoord(cord :Pair<Coordinate, TileAreaDir>) : Pair<Coordinate, TileAreaDir> {

    }

    private fun countScoreObject(cord: Coordinate, board: GameBoard, dir: TileAreaDir, visited: MutableSet<Pair<Coordinate, TileAreaDir>>) : Map<Color, Int> {
        visited.add(Pair(cord, dir))


    }

    override fun countScore(lastTile : Tile, board: GameBoard) : Map<Color, Int> {
        val visited = mutableSetOf<Pair<Coordinate, TileAreaDir>>()
        val cord = lastTile.coords ?: throw IllegalStateException("Tile in the board must have coordinates.")
        for (dir in TileAreaDir.entries) {
            val area = lastTile.getTileArea(dir)
            if (!visited.contains(Pair(cord, area))) {
                countScoreObject(cord, board, dir, visited)
            }
        }
    }

    override fun countFinalScore() : Map<Color, Int> {
        TODO("Not implemented.")
    }
}