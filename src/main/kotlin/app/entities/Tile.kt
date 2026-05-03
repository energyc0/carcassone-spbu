package app.entities

import app.services.Direction
import app.utils.AreaCoordinate
import app.utils.bottomCoordinates
import app.utils.leftCoordinates
import app.utils.rightCoordinates
import app.utils.topCoordinates

class Tile(
    private val tileLook: TileLook,
    val isStarting: Boolean = false,
) {

    fun getTileArea(cord: AreaCoordinate): GameObjectType = tileLook.getArea(cord)

    /**
     * Set given rotation for the TileLook.
     */
    fun setRotation(rotation: Rotation) {
        tileLook.setRotation(rotation)
    }

    /**
     * Get type of the connection (GameObjectType) from the given direction.
     */
    fun getConnectionType(direction: Direction): GameObjectType = tileLook.getConnectionType(direction)

    /**
     * up, left, down, right from 0 to 3 indices
     */
    fun getTileEdges(): Array<Array<GameObjectType>> {
        val edges = arrayOf(topCoordinates, leftCoordinates, bottomCoordinates, rightCoordinates)
        return Array(edges.size) { edge -> edges[edge].map { getTileArea(it) }.toTypedArray() }
    }
}
