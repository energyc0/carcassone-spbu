package app.entities

import app.context.IGameBoardReadForObject
import app.utils.TileCoordinate
import app.utils.Vec2
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

    /**
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

    /**
     * Just add meeple in the GameObject list. Do not set it on the board.
     */
    fun addMeep(meep: Meeple) {
        meeple.addLast(meep)
    }

    fun hasMeeple(): Boolean = meeple.isNotEmpty()
}
