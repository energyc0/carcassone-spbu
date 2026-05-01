package app.entities

import app.context.IGameBoardReadForObject
import app.utils.TileCoordinate

class GameObjectField : GameObject(GameObjectType.FIELD) {
    // Score for every built adjacent city
    private companion object {
        const val SCORE_INCREMENT = 3
    }

    // You can earn score only at the end of the game
    override fun getScoreInternal(
        start: TileCoordinate,
        board: IGameBoardReadForObject,
    ): MutableMap<Color, Int> = mutableMapOf()

    override fun getFinalScoreInternal(
        start: TileCoordinate,
        board: IGameBoardReadForObject,
    ): MutableMap<Color, Int> {
        if (!hasMeeple()) {
            return mutableMapOf()
        }

        val visited = mutableSetOf<TileCoordinate>()
        val toVisit = ArrayDeque(listOf(start))
        val visitedCities = mutableSetOf<GameObjectCity>()
        var score = 0

        while (toVisit.isNotEmpty()) {
            val curCoord = toVisit.removeFirst()
            visited.add(curCoord)

            val obj = board.getObject(curCoord) ?: continue
            if (obj.type == GameObjectType.CITY && !visitedCities.contains(obj)) {
                val city = obj as GameObjectCity
                visitedCities.add(city)
                if (city.isBuilt) {
                    score += SCORE_INCREMENT
                }
            } else if (obj.type == GameObjectType.FIELD) {
                curCoord.getAdjacent().forEach { i ->
                    if (!visited.contains(i)) {
                        toVisit.addLast(i)
                    }
                }
            }
        }
        return scoreForPlayer(score)
    }
}
