package app.context

import app.entities.GameObject
import app.entities.GameObjectDummy
import app.entities.GameObjectType
import app.entities.Meeple
import app.entities.Tile
import app.utils.TileCoordinate
import app.utils.Vec2

interface IGameBoardRead {
    fun getObject(coord: TileCoordinate): GameObject?
    fun getObjectDummy(coord: TileCoordinate): GameObjectDummy?
    fun getObjectType(coord: TileCoordinate): GameObjectType?
    fun getTile(coord: Vec2): Tile?
    fun getFreeSpace(): List<Vec2>

}

interface IGameBoardReadWrite :
    IGameBoardRead {
    fun insertTile(
        newTile: Tile,
        coordinate: Vec2,
    )

    fun setMeeple(
        m: Meeple,
        coord: TileCoordinate,
    )
}
