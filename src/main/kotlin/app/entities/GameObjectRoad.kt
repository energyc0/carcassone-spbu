package app.entities

import app.context.IGameBoardRead
import app.utils.TileCoordinate

class GameObjectRoad : GameObject(GameObjectType.ROAD) {
    private fun isBuilt(
        start: TileCoordinate,
        board: IGameBoardRead,
    ): Boolean {
        val visited = mutableSetOf<TileCoordinate>()
        val toVisit = ArrayDeque(listOf(start))

        while (toVisit.isNotEmpty()) {
            val curCoord = toVisit.removeFirst()
            visited.add(curCoord)

            val objDummy = board.getObjectDummy(curCoord) ?: return false

            curCoord.getAdjacent().forEach { i ->
                if (!visited.contains(i) && objDummy.type == type) {
                    toVisit.addLast(i)
                }
            }
        }
        return true
    }

    override fun getScoreInternal(
        start: TileCoordinate,
        board: IGameBoardRead,
    ): MutableMap<Color, Int> =
        if (isBuilt(start, board)) {
            scoreForPlayer(tilesCountOccupied)
        } else {
            mutableMapOf()
        }

    override fun getFinalScoreInternal(
        start: TileCoordinate,
        board: IGameBoardRead,
    ): MutableMap<Color, Int> = scoreForPlayer(tilesCountOccupied)
}
