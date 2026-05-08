package app.entities

import app.services.Direction
import app.utils.AreaCoordinate
import app.utils.TILE_AREA_SAMPLES
import app.utils.TILE_AREA_SAMPLES_TOTAL

enum class Rotation { STRAIGHT, RIGHT, LEFT, FLIPPED }

/*
 * TileLook is a class that is responsible for
 * tile appearance in the game.
 */
class TileLook(
    private val areas: Array<GameObjectType>,
    rot: Rotation = Rotation.STRAIGHT,
    val shields: Set<AreaCoordinate> = setOf(),
) {
    companion object {
        const val MID_SAMPLE = TILE_AREA_SAMPLES / 2
    }

    var rotation = rot
        private set

    init {
        require(areas.size == TILE_AREA_SAMPLES_TOTAL) { "Tile must contain $TILE_AREA_SAMPLES_TOTAL TileArea\'s" }
        require(shields.all { coord -> getArea(coord) == GameObjectType.CITY })
        { "Expected shields in cities (GameObjectType == CITY)." }
    }

    fun setRotation(rot: Rotation) {
        rotation = rot
    }

    fun getDrawData() {
        TODO("Need to implement GUI.")
    }

    fun getArea(cord: AreaCoordinate): GameObjectType {
        val curCord = rotateCord(cord)
        return areas[countCordIdx(curCord)]
    }

    fun getConnectionType(direction: Direction): GameObjectType =
        when (direction) {
            Direction.RIGHT -> getArea(AreaCoordinate(TILE_AREA_SAMPLES - 1, MID_SAMPLE))
            Direction.LEFT -> getArea(AreaCoordinate(0, MID_SAMPLE))
            Direction.UP -> getArea(AreaCoordinate(MID_SAMPLE, 0))
            Direction.DOWN -> getArea(AreaCoordinate(MID_SAMPLE, TILE_AREA_SAMPLES - 1))
        }

    private fun countCordIdx(cord: AreaCoordinate): Int = cord.y * TILE_AREA_SAMPLES + cord.x

    private fun rotateCord(cord: AreaCoordinate): AreaCoordinate {
        val maxX = TILE_AREA_SAMPLES - 1
        val maxY = TILE_AREA_SAMPLES - 1
        return when (rotation) {
            Rotation.STRAIGHT -> cord
            Rotation.FLIPPED -> AreaCoordinate(maxX - cord.x, maxY - cord.y)
            Rotation.RIGHT -> AreaCoordinate(cord.y, maxY - cord.x)
            Rotation.LEFT -> AreaCoordinate(maxX - cord.y, cord.x)
        }
    }

    fun hasShield(coord: AreaCoordinate): Boolean {
        if (shields.isEmpty()) {
            return false
        }
        val visited = mutableSetOf<AreaCoordinate>()
        val toVisit = ArrayDeque(listOf(coord))
        lateinit var curCoord: AreaCoordinate

        while (toVisit.isNotEmpty()) {
            curCoord = toVisit.removeFirst()
            if (shields.contains(curCoord)) {
                break
            }
            visited.add(curCoord)

            Direction.entries.forEach { dir ->
                val adjCoord = curCoord.getAdjacent(dir)
                if (!visited.contains(adjCoord)) {
                    toVisit.addLast(adjCoord)
                }
            }
        }
        return shields.contains(curCoord)
    }
}
