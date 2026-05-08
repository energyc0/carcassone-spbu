package app.entities

import app.context.IGameBoardRead
import app.utils.TileCoordinate

class GameObjectCity(
    hasShield: Boolean,
) : GameObject(GameObjectType.CITY) {
    private var _isBuilt = false
    var parentShieldsCount = if (hasShield) 1 else 0
        private set
    private val scoreInc = 2
    private val finalScoreInc = 1

    var isBuilt: Boolean
        get() {
            return (parent as GameObjectCity)._isBuilt
        }
        set(value) {
            (parent as GameObjectCity)._isBuilt = value
        }

    var shieldsCount: Int
        get() {
            return (parent as GameObjectCity).parentShieldsCount
        }
        set(value) {
            (parent as GameObjectCity).parentShieldsCount = value
        }

    private fun checkIsBuilt(
        start: TileCoordinate,
        board: IGameBoardRead,
    ): Boolean {
        if (!isBuilt) {
            val visited = mutableSetOf<TileCoordinate>()
            val toVisit = ArrayDeque(listOf(start))

            while (toVisit.isNotEmpty()) {
                val curCoord = toVisit.removeFirst()
                visited.add(curCoord)

                val t = board.getObjectType(curCoord) ?: return false

                curCoord.getAdjacent().forEach { i ->
                    if (!visited.contains(i) && t == type) {
                        toVisit.addLast(i)
                    }
                }
            }
            isBuilt = true
        }
        return true
    }

    // 2 points for every tile occupied and shield
    override fun getScoreInternal(
        start: TileCoordinate,
        board: IGameBoardRead,
    ): MutableMap<Color, Int> =
        if (checkIsBuilt(start, board)) {
            scoreForPlayer(scoreInc * (tilesCountOccupied + shieldsCount))
        } else {
            mutableMapOf()
        }

    // 1 point for every tile occupied and shield
    override fun getFinalScoreInternal(
        start: TileCoordinate,
        board: IGameBoardRead,
    ): MutableMap<Color, Int> = scoreForPlayer(finalScoreInc * (tilesCountOccupied + shieldsCount))

    override fun mergeWith(obj: GameObject) {
        super.mergeWith(obj)
        shieldsCount += (obj as GameObjectCity).shieldsCount
    }
}
