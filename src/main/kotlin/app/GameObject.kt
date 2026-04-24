package app

enum class GameObjectType {FIELD, CITY, MONASTERY, ROAD}

abstract class GameObject (val type: GameObjectType) {
    val meepleCount = mutableMapOf<Color, Int>()

    abstract fun getScore()

    fun addMeep(color: Color) {
        meepleCount[color] = meepleCount.getOrDefault(color, 0) + 1
    }
}