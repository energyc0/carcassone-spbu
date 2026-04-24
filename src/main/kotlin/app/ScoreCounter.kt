package app

class ScoreCounter : IScoreCounter {
    private fun countScoreObject(
        cord: TileCoordinate,
        startObj: GameObject,
        board: IGameBoardReadObject,
    ): MutableMap<Color, Int> {
        val visited = mutableSetOf<TileCoordinate>()
        val toVisit = ArrayDeque(listOf(cord))

        val result = mutableMapOf<Color, Int>()
        while (toVisit.isNotEmpty()) {
            val curCoord = toVisit.removeFirst()
            visited.add(curCoord)

            val curObj = board.getObject(curCoord) ?: continue
            if (!startObj.canBeBuilt(curObj)) {
                return mutableMapOf<Color, Int>()
            }

            curCoord.getAdjacent().forEach { i ->
                if (!visited.contains(i)) {
                    toVisit.addLast(i)
                }
            }
        }

        return startObj.getScore()
    }

    override fun countScore(
        lastCoord: Vec2,
        board: IGameBoardReadObject,
    ): Map<Color, Int> {
        val visitedObj = mutableSetOf<GameObject>()
        var result = mutableMapOf<Color, Int>()
        for (x in 0..TILE_AREA_SAMPLES) {
            for (y in 0..TILE_AREA_SAMPLES) {
                val tileCoordinate = TileCoordinate(lastCoord, AreaCoordinate(x, y))
                val obj =
                    board.getObject(tileCoordinate)
                        ?: throw IllegalStateException("Board cannot contain empty elements.")
                if (visitedObj.contains(obj)) {
                    continue
                }
                visitedObj.add(obj)
                result = mergeColorIntMaps(result, countScoreObject(tileCoordinate, obj, board))
            }
        }
        return result
    }

    override fun countFinalScore(): Map<Color, Int> {
        TODO("Not implemented.")
    }
}
