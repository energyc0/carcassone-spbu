package app.entities

import app.services.Direction
import app.utils.AreaCoordinate
import app.utils.TILE_AREA_SAMPLES
import app.utils.Vec2
import app.utils.bottomCoordinates
import app.utils.leftCoordinates
import app.utils.rightCoordinates
import app.utils.topCoordinates

class Tile(
    val isStarting: Boolean,
    private val tileLook: TileLook,
) {
    var coords: Vec2? = null
        private set

    fun getTileArea(cord: AreaCoordinate): GameObjectType = tileLook.getArea(cord)

    fun setRotation(rotation: Rotation) {
        tileLook.setRotation(rotation)
    }

    fun setTile(coord: Vec2) {
        require(coords == null) { "Tile is already set. " }
        coords = coord
    }

    fun getDirType(direction: Direction) : GameObjectType = tileLook.getDirType(direction)

    /**
     * up, left, down, right from 0 to 3 indices
     */
    fun getTileEdges(): Array<Array<GameObjectType>> {
        val edges = arrayOf(topCoordinates, leftCoordinates, bottomCoordinates, rightCoordinates)
        return Array(edges.size) { edge -> edges[edge].map { getTileArea(it) }.toTypedArray() }
    }
}
