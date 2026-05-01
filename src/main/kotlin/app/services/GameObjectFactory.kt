package app.services

import app.entities.GameObject
import app.entities.GameObjectCity
import app.entities.GameObjectField
import app.entities.GameObjectMonastery
import app.entities.GameObjectRoad
import app.entities.GameObjectType

class GameObjectFactory {
    /**
     * Create GameObject of the given type. Throws IllegalArgumentException if type is 'CROSSROAD'
     */
    fun createObject(type: GameObjectType): GameObject =
        when (type) {
            GameObjectType.CITY -> {
                GameObjectCity()
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
                throw IllegalArgumentException("Cannot create GameObject of type \"CROSSROAD\".")
            }
        }
}
