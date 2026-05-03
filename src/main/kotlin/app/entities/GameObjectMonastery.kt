package app.entities

import app.context.IGameBoardRead
import app.utils.TileCoordinate
import app.utils.Vec2

class GameObjectMonastery : GameObject(GameObjectType.MONASTERY) {
    companion object {
        const val MONASTERY_TOTAL_SCORE = 9
    }

    private fun isBuilt(
        start: TileCoordinate,
        board: IGameBoardRead,
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
        board: IGameBoardRead,
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

    override fun getScoreInternal(
        start: TileCoordinate,
        board: IGameBoardRead,
    ): MutableMap<Color, Int> =
        if (!hasMeeple() || !isBuilt(start, board)) {
            mutableMapOf()
        } else {
            scoreForPlayer(MONASTERY_TOTAL_SCORE)
        }

    override fun getFinalScoreInternal(
        start: TileCoordinate,
        board: IGameBoardRead,
    ): MutableMap<Color, Int> {
        if (!hasMeeple()) {
            return mutableMapOf()
        }
        return scoreForPlayer(countAdjacentTiles(start, board))
    }
}
