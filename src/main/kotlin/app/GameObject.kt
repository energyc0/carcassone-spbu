package app

enum class GameObjectType {FIELD, CITY, MONASTERY, ROAD}

abstract class GameObject (val type: GameObjectType) {
    val meepleCount = mutableMapOf<Color, Int>()

    abstract fun getScore()

    fun addMeep(color: Color) {
        meepleCount[color] = meepleCount.getOrDefault(color, 0) + 1
    }
}

class GameObjectMonastery : GameObject (GameObjectType.MONASTERY){
    override fun getScore() {
        TODO("Not yet implemented")
    }
}

class GameObjectField : GameObject (GameObjectType.FIELD){
    override fun getScore() {
        TODO("Not yet implemented")
    }
}

class GameObjectRoad : GameObject (GameObjectType.ROAD){
    override fun getScore() {
        TODO("Not yet implemented")
    }
}

class GameObjectCity : GameObject (GameObjectType.CITY){
    override fun getScore() {
        TODO("Not yet implemented")
    }
}

class GameObjectFactory {
    fun createObject(type: GameObjectType) : GameObject{
        return when (type) {
            GameObjectType.CITY -> GameObjectCity()
            GameObjectType.FIELD -> GameObjectField()
            GameObjectType.ROAD -> GameObjectRoad()
            GameObjectType.MONASTERY -> GameObjectMonastery()
        }
    }
}