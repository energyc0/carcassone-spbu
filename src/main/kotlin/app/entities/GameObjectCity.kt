package app.entities

import app.context.IGameBoardRead
import app.utils.TileCoordinate

/*
 *  Need to implement shield technique
 */
class GameObjectCity : GameObject(GameObjectType.CITY) {
    private var _isBuilt = false
    private val scoreInc = 2

    var isBuilt: Boolean
        get() {
            return (parent as GameObjectCity)._isBuilt
        }
        set(value) {
            (parent as GameObjectCity)._isBuilt = value
        }

    private fun checkIsBuilt(
        start: TileCoordinate,
        board: IGameBoardRead,
    ): Boolean {
        if (!isBuilt) {
            val visited = mutableSetOf<TileCoordinate>()
            val toVisit = ArrayDeque(listOf(start))

            while (toVisit.isNotEmpty()) {
                val curCoord = toVisit.removeFirst()
                visited.add(curCoord)

                val t = board.getObjectType(curCoord) ?: return false

                curCoord.getAdjacent().forEach { i ->
                    if (!visited.contains(i) && t == type) {
                        toVisit.addLast(i)
                    }
                }
            }
            isBuilt = true
        }
        return true
    }

    override fun getScoreInternal(
        start: TileCoordinate,
        board: IGameBoardRead,
    ): MutableMap<Color, Int> =
        if (checkIsBuilt(start, board)) {
            scoreForPlayer(scoreInc * tilesCountOccupied)
        } else {
            mutableMapOf()
        }

    // 1 point for every tile
    override fun getFinalScoreInternal(
        start: TileCoordinate,
        board: IGameBoardRead,
    ): MutableMap<Color, Int> = scoreForPlayer(tilesCountOccupied)
}
