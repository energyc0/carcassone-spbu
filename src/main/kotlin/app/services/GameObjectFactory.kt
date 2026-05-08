package app.services

import app.entities.GameObject
import app.entities.GameObjectCity
import app.entities.GameObjectCrossroad
import app.entities.GameObjectDummy
import app.entities.GameObjectField
import app.entities.GameObjectMonastery
import app.entities.GameObjectRoad
import app.entities.GameObjectType
import app.entities.Tile
import app.utils.AreaCoordinate

class GameObjectFactory {
    /**
     * Create GameObject of the given type from the tile.
     */
    fun createObject(
        tile: Tile,
        coord: AreaCoordinate,
    ): GameObjectDummy =
        when (tile.getTileArea(coord)) {
            GameObjectType.CITY -> {
                GameObjectCity(tile.hasShield(coord))
            }

            GameObjectType.FIELD -> {
                GameObjectField()
            }

            GameObjectType.ROAD -> {
                GameObjectRoad()
            }

            GameObjectType.MONASTERY -> {
                GameObjectMonastery()
            }

            GameObjectType.CROSSROAD -> {
                GameObjectCrossroad()
            }
        }

    /**
     * Create GameObject of the given type. For tests.
     */
    internal fun createObjectTest(type: GameObjectType): GameObjectDummy =
        when (type) {
            GameObjectType.CITY -> {
                GameObjectCity(false)
            }

            GameObjectType.FIELD -> {
                GameObjectField()
            }

            GameObjectType.ROAD -> {
                GameObjectRoad()
            }

            GameObjectType.MONASTERY -> {
                GameObjectMonastery()
            }

            GameObjectType.CROSSROAD -> {
                GameObjectCrossroad()
            }
        }
}
