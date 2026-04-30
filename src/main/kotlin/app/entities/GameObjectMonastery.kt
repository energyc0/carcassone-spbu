package app.entities

import app.context.IGameBoardReadForObject
import app.utils.TileCoordinate
import app.utils.Vec2

class GameObjectMonastery : GameObject(GameObjectType.MONASTERY) {
    private companion object {
        const val MONASTERY_TOTAL_SCORE = 9
    }

    private fun isBuilt(
        start: TileCoordinate,
        board: IGameBoardReadForObject,
    ): Boolean {
        for (x in -1..1) {
            for (y in -1..1) {
                val coord = Vec2(start.tileCoord.x + x, start.tileCoord.y + y)
                if (board.getTile(coord) == null) {
                    return false
                }
            }
        }
        return true
    }

    private fun countAdjacentTiles(
        start: TileCoordinate,
        board: IGameBoardReadForObject,
    ): Int {
        var result = 0
        for (x in -1..1) {
            for (y in -1..1) {
                val coord = Vec2(start.tileCoord.x + x, start.tileCoord.y + y)
                if (board.getTile(coord) != null) {
                    result++
                }
            }
        }
        return result
    }

    override fun getScore(
        start: TileCoordinate,
        board: IGameBoardReadForObject,
    ): MutableMap<Color, Int> =
        if (meeple.isEmpty() || !isBuilt(start, board)) {
            mutableMapOf()
        } else {
            scoreForPlayer(MONASTERY_TOTAL_SCORE)
        }

    override fun getFinalScore(
        start: TileCoordinate,
        board: IGameBoardReadForObject,
    ): MutableMap<Color, Int> {
        if (meeple.isEmpty()) {
            return mutableMapOf()
        }
        return scoreForPlayer(countAdjacentTiles(start, board))
    }
}