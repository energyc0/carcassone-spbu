package app.services

import app.context.IGameBoardReadForCounter
import app.entities.Color
import app.entities.GameObject
import app.entities.GameObjectDummy
import app.entities.GameObjectType
import app.utils.AreaCoordinate
import app.utils.TILE_AREA_SAMPLES
import app.utils.TileCoordinate
import app.utils.Vec2

/*
 * Merge two maps and sum up their values.
 */
fun mergeColorIntMaps(
    a: MutableMap<Color, Int>,
    b: MutableMap<Color, Int>,
): MutableMap<Color, Int> =
    (a.keys + b.keys)
        .associateWith { key ->
            a.getOrDefault(key, 0) + b.getOrDefault(key, 0)
        }.toMutableMap()

class ScoreCounter : IScoreCounter {
    override fun countScore(
        lastCoord: Vec2,
        board: IGameBoardReadForCounter,
    ): Map<Color, Int> {
        val visitedObj = mutableSetOf<GameObjectDummy>()
        var result = mutableMapOf<Color, Int>()
        for (x in 0..TILE_AREA_SAMPLES) {
            for (y in 0..TILE_AREA_SAMPLES) {
                val tileCoordinate = TileCoordinate(lastCoord, AreaCoordinate(x, y))
                val objDummy =
                    board.getObjectDummy(tileCoordinate)
                        ?: throw IllegalStateException("Board cannot contain empty elements.")
                if (visitedObj.contains(objDummy) || objDummy.type == GameObjectType.CROSSROAD) {
                    continue
                }
                visitedObj.add(objDummy)
                val obj = objDummy as GameObject
                result = mergeColorIntMaps(result, obj.getScore(tileCoordinate, board))
            }
        }
        return result
    }

    // Count score for one tile
    private fun countFinalScoreTile(
        coord: Vec2,
        visitedObj: MutableSet<GameObject>,
        result: MutableMap<Color, Int>,
        board: IGameBoardReadForCounter,
    ) {
        for (x in 0..TILE_AREA_SAMPLES) {
            for (y in 0..TILE_AREA_SAMPLES) {
                val tileCoordinate = TileCoordinate(coord, AreaCoordinate(x, y))
                val obj =
                    board.getObject(tileCoordinate)
                        ?: throw IllegalStateException("Board cannot contain empty elements.")
                if (visitedObj.contains(obj)) {
                    continue
                }
                visitedObj.add(obj)
                // Copy new data into result variable
                mergeColorIntMaps(result, obj.getFinalScore(tileCoordinate, board)).forEach { (color, score) ->
                    result[color] = score
                }
            }
        }
    }

    // Check every tile and count score for objects
    override fun countFinalScore(board: IGameBoardReadForCounter): Map<Color, Int> {
        val visitedObj = mutableSetOf<GameObject>()
        val visitedTiles = mutableSetOf<Vec2>()
        val toVisit = ArrayDeque(listOf(Vec2(0, 0)))
        val result = mutableMapOf<Color, Int>()

        while (toVisit.isNotEmpty()) {
            val curCoord = toVisit.removeFirst()
            visitedTiles.add(curCoord)
            if (board.getTile(curCoord) == null) continue
            countFinalScoreTile(curCoord, visitedObj, result, board)

            curCoord.getAdjacent().forEach { i ->
                if (!visitedTiles.contains(i)) {
                    toVisit.addLast(i)
                }
            }
        }
        return result.toMap()
    }
}
