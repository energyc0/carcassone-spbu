package app

import kotlin.collections.mutableMapOf

enum class GameObjectType { FIELD, CITY, MONASTERY, ROAD }

abstract class GameObject(
    val type: GameObjectType,
) {
    var meepleCount = mutableMapOf<Color, Int>()

    abstract fun getScore(): MutableMap<Color, Int>

    /*
     * A condition whether the GameObject can be built near the given other GameObject.
     */
    abstract fun canBeBuilt(obj: GameObject): Boolean

    fun addMeep(color: Color) {
        meepleCount[color] = meepleCount.getOrDefault(color, 0) + 1
    }
}

class GameObjectMonastery : GameObject(GameObjectType.MONASTERY) {
    override fun getScore(): MutableMap<Color, Int> {
        TODO("Not yet implemented")
    }

    override fun canBeBuilt(obj: GameObject): Boolean {
        TODO("Not yet implemented")
    }
}

class GameObjectField : GameObject(GameObjectType.FIELD) {
    override fun getScore(): MutableMap<Color, Int> {
        TODO("Not yet implemented")
    }

    override fun canBeBuilt(obj: GameObject): Boolean {
        TODO("Not yet implemented")
    }
}

class GameObjectRoad : GameObject(GameObjectType.ROAD) {
    override fun getScore(): MutableMap<Color, Int> {
        TODO("Not yet implemented")
    }

    override fun canBeBuilt(obj: GameObject): Boolean {
        TODO("Not yet implemented")
    }
}

class GameObjectCity : GameObject(GameObjectType.CITY) {
    override fun getScore(): MutableMap<Color, Int> {
        TODO("Not yet implemented")
    }

    override fun canBeBuilt(obj: GameObject): Boolean {
        TODO("Not yet implemented")
    }
}

class GameObjectFactory {
    fun createObject(type: GameObjectType): GameObject =
        when (type) {
            GameObjectType.CITY -> GameObjectCity()
            GameObjectType.FIELD -> GameObjectField()
            GameObjectType.ROAD -> GameObjectRoad()
            GameObjectType.MONASTERY -> GameObjectMonastery()
        }
}
