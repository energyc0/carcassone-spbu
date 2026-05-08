package app.entities

import app.context.IGameBoardRead
import app.utils.TileCoordinate
import kotlin.collections.mutableMapOf

enum class GameObjectType { FIELD, CITY, MONASTERY, ROAD, CROSSROAD }

abstract class GameObjectDummy(
    val type: GameObjectType,
)

abstract class GameObject(
    gameObjectType: GameObjectType,
) : GameObjectDummy(gameObjectType) {
    private var parentMeeple = mutableListOf<Meeple>()
    private var parentTilesOccupied = 1
    private var parentHasGottenScore = false
    private var actualParent: GameObject? = null

    init {
        require(type != GameObjectType.CROSSROAD) { "There cannot be GameObject of type \"CROSSROAD\"." }
    }

    var tilesCountOccupied: Int
        get() {
            return parent.parentTilesOccupied
        }
        private set(value) {
            parent.parentTilesOccupied = value
        }

    private val meeple: MutableList<Meeple>
        get() {
            return parent.parentMeeple
        }
    private var hasGottenScore: Boolean
        get() {
            return parent.parentHasGottenScore
        }
        set(value: Boolean) {
            parent.parentHasGottenScore = value
        }

    /** Parent has always the same type as child GameObject */
    protected var parent: GameObject
        get() {
            var curParent = actualParent ?: return this

            while (curParent.actualParent != null) {
                val newParent = curParent.actualParent ?: break
                curParent = newParent
                actualParent = curParent
            }

            return curParent
        }
        set(value) {
            if (actualParent == null) {
                actualParent = value.parent
            } else {
                actualParent?.actualParent = value.parent
            }
        }

    /** Children need to implement checking whether the object is built and returning score */
    protected abstract fun getScoreInternal(
        start: TileCoordinate,
        board: IGameBoardRead,
    ): MutableMap<Color, Int>

    /** Children need to implement returning score */
    protected abstract fun getFinalScoreInternal(
        start: TileCoordinate,
        board: IGameBoardRead,
    ): MutableMap<Color, Int>

    /**
     * Try to BFS the object. check whether the object is built
     * If the object is built, return score for every player and return meeple.
     */
    fun getScore(
        start: TileCoordinate,
        board: IGameBoardRead,
    ): MutableMap<Color, Int> {
        if (hasGottenScore) {
            return mutableMapOf()
        }
        val score = parent.getScoreInternal(start, board)
        if (score.isNotEmpty()) {
            hasGottenScore = true
            parent.returnMeeple()
        }
        return score
    }

    fun getFinalScore(
        start: TileCoordinate,
        board: IGameBoardRead,
    ): MutableMap<Color, Int> {
        if (hasGottenScore) {
            return mutableMapOf()
        }
        val score = parent.getFinalScoreInternal(start, board)
        hasGottenScore = true
        parent.returnMeeple()
        return score
    }

    private fun returnMeeple() {
        meeple.forEach { i -> i.returnToPlayer() }
        meeple.clear()
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

    open fun mergeWith(obj: GameObject) {
        require(obj.type == type) { "Cannot merge GameObject`s of different type" }
        meeple.addAll(obj.meeple)
        tilesCountOccupied += obj.tilesCountOccupied
        obj.parent = parent
    }

    /**
     * Just add meeple in the GameObject list. Do not set it on the board.
     * Throw IllegalStateException if object already has meeple.
     */
    fun addMeep(meep: Meeple) {
        if (hasMeeple()) {
            throw IllegalStateException("Object has already meeple.")
        }
        meeple.addLast(meep)
    }

    fun hasMeeple(): Boolean = meeple.isNotEmpty()

    override fun equals(other: Any?): Boolean {
        if (other is GameObject) {
            return other.parent === this.parent
        }
        return false
    }

    override fun hashCode(): Int = System.identityHashCode(parent)
}
