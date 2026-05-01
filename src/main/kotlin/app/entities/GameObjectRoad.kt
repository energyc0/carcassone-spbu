package app.entities

import app.context.IGameBoardReadForObject
import app.utils.TileCoordinate

class GameObjectRoad : GameObject(GameObjectType.ROAD) {
    private fun isBuilt(
        start: TileCoordinate,
        board: IGameBoardReadForObject,
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
        board: IGameBoardReadForObject,
    ): MutableMap<Color, Int> =
        if (!hasMeeple() || !isBuilt(start, board)) {
            mutableMapOf()
        } else {
            scoreForPlayer(tilesCountOccupied)
        }

    override fun getFinalScoreInternal(
        start: TileCoordinate,
        board: IGameBoardReadForObject,
    ): MutableMap<Color, Int> = scoreForPlayer(tilesCountOccupied)
}
