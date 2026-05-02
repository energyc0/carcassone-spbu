package app.entities

import app.services.GameObjectFactory
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

internal class GameObjectTest {
    lateinit var factory: GameObjectFactory

    @BeforeEach
    fun setUp() {
        factory = GameObjectFactory()
    }

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
            assert(!obj.hasMeeple())
            assert(obj.type == typeList[index])
            assert(!obj.hasGottenScore)
        }

        GameObjectType.entries.forEach { type ->
           assert(type == factory.createObject(type).type)
        }
    }

    @Test
    @DisplayName("GameObject add meeple test")
    fun addMeepleTest() {
        val meeple = Meeple(Color.RED)
        val meepleToThrow = Meeple(Color.GREEN)
        GameObjectType.entries.forEach { type ->
            if (type == GameObjectType.CROSSROAD) return@forEach

            val obj = factory.createObject(type) as GameObject
            obj.addMeep(meeple)
            assert(!meeple.isOnBoard())
            assert(obj.hasMeeple())
            assertThrows<IllegalStateException> { obj.addMeep(meepleToThrow) }
        }
    }

    @Test
    @DisplayName("Merged roads should calculate score based on total tiles")
    fun mergeScoreTest() {
        val road1 = factory.createObject(GameObjectType.ROAD) as GameObject
        val road2 = factory.createObject(GameObjectType.ROAD) as GameObject
        assertDoesNotThrow {
            road1.mergeWith(road2)
        }

        assert(road2.tilesCountOccupied == 2)
        assert(road1.tilesCountOccupied == 2)
    }

    @Test
    @DisplayName("Merge two fields should combine correctly")
    fun meepleMergeTest() {
        val field1 = factory.createObject(GameObjectType.FIELD) as GameObject
        val field2 = factory.createObject(GameObjectType.FIELD) as GameObject
        val meeple = Meeple(Color.RED)

        field1.addMeep(meeple)
        field1.mergeWith(field2)

        assert(field1.hasMeeple())
        assert(field2.hasMeeple())
    }

    @Test
    @DisplayName("Merge different types should throw exception")
    fun differentTypeMergeTest() {
        val road = factory.createObject(GameObjectType.ROAD) as GameObject
        val field = factory.createObject(GameObjectType.FIELD) as GameObject

        assertThrows<IllegalArgumentException> {
            road.mergeWith(field)
        }
    }

    @Test
    @DisplayName("Chained merges should resolve to root parent")
    fun resolveParentTest() {
        val roads = Array(5) { factory.createObject(GameObjectType.ROAD) as GameObject }
        val meeple = Meeple(Color.RED)

        roads[0].addMeep(meeple)

        roads[0].mergeWith(roads[1])
        roads[0].mergeWith(roads[2])
        var root = roads[0]
        for (i in 0..2) {
            assert(roads[i].tilesCountOccupied == 3)
            assert(roads[i].hasMeeple())
            assert(roads[i] == root)
        }

        roads[3].mergeWith(roads[4])
        // Objects have the same root
        root = roads[4]
        for (i in 3..4) {
            assert(roads[i].tilesCountOccupied == 2)
            assert(!roads[i].hasMeeple())
            assert(roads[i] == root)
        }

        roads[3].mergeWith(roads[1])
        root = roads[3]
        roads.forEach { road ->
            assert(road.hasMeeple())
            assertEquals(5, road.tilesCountOccupied)
            assert(road == root)
        }
    }
}
