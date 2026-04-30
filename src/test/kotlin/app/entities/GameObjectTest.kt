package app.entities

import app.services.GameObjectFactory
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

internal class GameObjectTest {
    @Test
    @DisplayName("GameObjectTest initialization test")
    fun initTest() {
        val city = GameObjectCity()
        val field = GameObjectField()
        val road = GameObjectRoad()
        val monastery = GameObjectMonastery()

        val objList = listOf(city, field, road, monastery)
        val typeList = listOf(GameObjectType.CITY, GameObjectType.FIELD, GameObjectType.ROAD, GameObjectType.MONASTERY)

        objList.forEachIndexed { index, obj ->
            assert(obj.meeple.isEmpty())
            assert(obj.type == typeList[index])
            assert(!obj.hasGottenScore)
        }

        val factory = GameObjectFactory()
        GameObjectType.entries.forEach { type->
            if (type == GameObjectType.CROSSROAD) {
                assertThrows(IllegalArgumentException::class.java) { factory.createObject(type) }
            } else {
                assertDoesNotThrow { factory.createObject(type) }
            }
        }
    }

    @Test
    @DisplayName("GameObject add meeple test")
    fun addMeepleTest() {
        val factory = GameObjectFactory()
        val meeple = Meeple(Color.RED)
        GameObjectType.entries.forEach { type->
            if (type == GameObjectType.CROSSROAD) return@forEach

            val obj = factory.createObject(type)
            obj.addMeep(meeple)
            assert(!meeple.isOnBoard())
            assert(obj.hasMeeple())
        }
    }
}