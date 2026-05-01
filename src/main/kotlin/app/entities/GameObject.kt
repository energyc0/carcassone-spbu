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
    private var meeple = mutableListOf<Meeple>()
    private var rootTilesCountOccupied = 1
    var hasGottenScore = false
        private set
    private var parentObj: GameObject? = null

    init {
        require(type != GameObjectType.CROSSROAD) { "There cannot be GameObject of type \"CROSSROAD\"." }
    }

    val tilesCountOccupied: Int
        get() {
            return traverseToParent().rootTilesCountOccupied
        }

    private fun traverseToParent(): GameObject {
        var parent = parentObj ?: return this

        while (parent.parentObj != null) {
            val newParent = parent.parentObj ?: break
            parent = newParent
            parentObj = parent
        }

        return parent
    }

    protected abstract fun getScoreInternal(
        start: TileCoordinate,
        board: IGameBoardReadForObject,
    ): MutableMap<Color, Int>

    protected abstract fun getFinalScoreInternal(
        start: TileCoordinate,
        board: IGameBoardReadForObject,
    ): MutableMap<Color, Int>

    /**
     * Try to BFS the object. check whether the object is built
     * If the object is built, return score for every player and return meeple.
     */
    fun getScore(
        start: TileCoordinate,
        board: IGameBoardReadForObject,
    ): MutableMap<Color, Int> = traverseToParent().getScoreInternal(start, board)

    fun getFinalScore(
        start: TileCoordinate,
        board: IGameBoardReadForObject,
    ): MutableMap<Color, Int> = traverseToParent().getFinalScoreInternal(start, board)

    private fun returnMeeple() {
        traverseToParent().meeple.forEach { i -> i.returnToPlayer() }
    }

    protected fun scoreForPlayer(score: Int): MutableMap<Color, Int> {
        val count = mutableMapOf<Color, Int>()
        val obj = traverseToParent()

        obj.meeple.forEach { m ->
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

    fun mergeWith(obj: GameObject) {
        require(obj.type == type) { "Cannot merge GameObject`s of different type" }
        val thisParent = traverseToParent()
        val objParent = obj.traverseToParent()
        thisParent.meeple.addAll(objParent.meeple)
        thisParent.rootTilesCountOccupied += objParent.tilesCountOccupied
        objParent.parentObj = thisParent
    }

    /**
     * Just add meeple in the GameObject list. Do not set it on the board.
     */
    fun addMeep(meep: Meeple) {
        traverseToParent().meeple.addLast(meep)
    }

    fun hasMeeple(): Boolean = traverseToParent().meeple.isNotEmpty()

    override fun equals(other: Any?): Boolean {
        if (other is GameObject) {
            return other.traverseToParent() === this.traverseToParent()
        }
        return false
    }

    override fun hashCode(): Int {
        var result = tilesCountOccupied
        result = 31 * result + hasGottenScore.hashCode()
        result = 31 * result + meeple.hashCode()
        result = 31 * result + (parentObj?.hashCode() ?: 0)
        return result
    }
}
