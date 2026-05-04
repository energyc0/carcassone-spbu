package app.utils

import app.services.Direction
import org.junit.jupiter.api.DisplayName
import kotlin.math.abs
import kotlin.test.Test

internal class Vec2Test {
    @Test
    @DisplayName("Vec2 equality test")
    fun equalityTest() {
        val v1 = Vec2(1, 1)
        val v2 = Vec2(1, 1)
        val v3 = Vec2(-1, 2)

        assert(v1 == v2)
        assert(v1 == v1)
        assert(v1 != v3)
        assert(v3 != v1)
    }

    @Test
    @DisplayName("Find direction between adjacent points")
    fun directionTest() {
        val vec1: Vec2 = Vec2(0, 0)
        val vec2: Vec2 = Vec2(1, 0)
        val vec3: Vec2 = Vec2(0, 1)
        val vec4: Vec2 = Vec2(-1, 0)

        assert(vec1.getDirectionTo(vec2) == Direction.RIGHT)
        assert(vec1.getDirectionTo(vec3) == Direction.UP)
        assert(vec1.getDirectionTo(vec4) == Direction.LEFT)
        assert(vec3.getDirectionTo(vec1) == Direction.DOWN)
    }

    @Test
    @DisplayName("Find adjacent vectors")
    fun adjacentTest() {
        val vec = Vec2(1, 3)

        vec.getAdjacent().forEach { i ->
            assert(abs(vec.x - i.x) + abs(vec.y - i.y) == 1)
        }
    }
}
