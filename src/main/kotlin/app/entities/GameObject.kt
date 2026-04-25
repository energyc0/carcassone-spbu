package app

import kotlin.collections.mutableMapOf

enum class GameObjectType { FIELD, CITY, MONASTERY, ROAD, CROSSROAD }

abstract class GameObjectDummy(
    val type: GameObjectType,
)

abstract class GameObject(
    gameObjectType: GameObjectType,
) : GameObjectDummy(gameObjectType) {
    var meeple = mutableListOf<Meeple>()
    protected var tilesCountOccupied = 1
    var hasGottenScore = false
        private set

    init {
        require(type != GameObjectType.CROSSROAD) { "There cannot be GameObject of type \"CROSSROAD\"." }
    }

    /*
     * Try to BFS the object. check whether the object is built
     * If the object is built, return score for every player and return meeple.
     */
    abstract fun getScore(
        start: TileCoordinate,
        board: IGameBoardReadForObject,
    ): MutableMap<Color, Int>

    abstract fun getFinalScore(
        start: TileCoordinate,
        board: IGameBoardReadForObject,
    ): MutableMap<Color, Int>

    private fun returnMeeple() {
        meeple.forEach { i -> i.returnToPlayer() }
    }

    protected fun scoreForPlayer(score: Int): MutableMap<Color, Int> {
        val count = mutableMapOf<Color, Int>()

        meeple.forEach { m ->
            count[m.color] = count.getOrDefault(m.color, 0) + 1
        }

        val maxCount = count.values.maxOrNull() ?: 0
        if (count.isEmpty() || maxCount == 0) return mutableMapOf()

        val result = mutableMapOf<Color, Int>()
        count.forEach { keyVal ->
            if (keyVal.value == maxCount) {
                result[keyVal.key] = score
            }
        }

        return result
    }

    fun addMeep(meep: Meeple) {
        meeple.addLast(meep)
    }

    fun hasMeeple(): Boolean = meeple.isNotEmpty()
}

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

class GameObjectField : GameObject(GameObjectType.FIELD) {
    // Score for every built adjacent city
    private companion object {
        const val SCORE_INCREMENT = 3
    }

    // You can earn score only at the end of the game
    override fun getScore(
        start: TileCoordinate,
        board: IGameBoardReadForObject,
    ): MutableMap<Color, Int> = mutableMapOf()

    override fun getFinalScore(
        start: TileCoordinate,
        board: IGameBoardReadForObject,
    ): MutableMap<Color, Int> {
        if (meeple.isNotEmpty()) {
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

    override fun getScore(
        start: TileCoordinate,
        board: IGameBoardReadForObject,
    ): MutableMap<Color, Int> =
        if (meeple.isEmpty() || !isBuilt(start, board)) {
            mutableMapOf()
        } else {
            scoreForPlayer(tilesCountOccupied)
        }

    override fun getFinalScore(
        start: TileCoordinate,
        board: IGameBoardReadForObject,
    ): MutableMap<Color, Int> = scoreForPlayer(tilesCountOccupied)
}

/*
 *  Need to implement shield technique
 */
class GameObjectCity : GameObject(GameObjectType.CITY) {
    var isBuilt = false
        private set
    private val scoreInc = 2

    override fun getScore(
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
    override fun getFinalScore(
        start: TileCoordinate,
        board: IGameBoardReadForObject,
    ): MutableMap<Color, Int> = scoreForPlayer(tilesCountOccupied)
}

class GameObjectFactory {
    fun createObject(type: GameObjectType): GameObject =
        when (type) {
            GameObjectType.CITY -> {
                GameObjectCity()
            }

            GameObjectType.FIELD -> {
                GameObjectField()
            }

            GameObjectType.ROAD -> {
                GameObjectRoad()
            }

            GameObjectType.MONASTERY -> {
                GameObjectMonastery()
            }

            GameObjectType.CROSSROAD -> {
                throw IllegalArgumentException("Cannot create GameObject of type \"CROSSROAD\".")
            }
        }
}
