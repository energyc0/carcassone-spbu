package app.entities

import app.context.IGameBoardReadForObject
import app.utils.TileCoordinate

/*
 *  Need to implement shield technique
 */
class GameObjectCity : GameObject(GameObjectType.CITY) {
    var isBuilt = false
        private set
    private val scoreInc = 2

    override fun getScoreInternal(
        start: TileCoordinate,
        board: IGameBoardReadForObject,
    ): MutableMap<Color, Int> {
        val visited = mutableSetOf<TileCoordinate>()
        val toVisit = ArrayDeque(listOf(start))

        while (toVisit.isNotEmpty()) {
            val curCoord = toVisit.removeFirst()
            visited.add(curCoord)

            val t = board.getObjectType(curCoord) ?: return mutableMapOf()

            curCoord.getAdjacent().forEach { i ->
                if (!visited.contains(i) && t == type) {
                    toVisit.addLast(i)
                }
            }
        }
        return scoreForPlayer(scoreInc * tilesCountOccupied)
    }

    // 1 point for every tile
    override fun getFinalScoreInternal(
        start: TileCoordinate,
        board: IGameBoardReadForObject,
    ): MutableMap<Color, Int> = scoreForPlayer(tilesCountOccupied)
}
