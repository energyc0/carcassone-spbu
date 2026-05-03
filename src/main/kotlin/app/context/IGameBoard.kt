package app.context

import app.entities.GameObject
import app.entities.GameObjectDummy
import app.entities.GameObjectType
import app.entities.Meeple
import app.entities.Tile
import app.utils.TileCoordinate
import app.utils.Vec2

interface IGameBoardReadObject {
    fun getObject(coord: TileCoordinate): GameObject?

    fun getObjectDummy(coord: TileCoordinate): GameObjectDummy?
}

interface IGameBoardReadObjectType {
    fun getObjectType(coord: TileCoordinate): GameObjectType?
}

interface IGameBoardReadTile {
    fun getTile(coord: Vec2): Tile?
}

interface IGameBoardReadForObject :
    IGameBoardReadTile,
    IGameBoardReadObjectType,
    IGameBoardReadObject

interface IGameBoardReadForCounter :
    IGameBoardReadForObject,
    IGameBoardReadObject

interface IGameBoardReadTileSpace : IGameBoardReadTile {
    fun getFreeSpace(): List<Vec2>
}

interface IGameBoardReadWrite :
    IGameBoardReadObject,
    IGameBoardReadObjectType,
    IGameBoardReadTileSpace,
    IGameBoardReadForObject,
    IGameBoardReadForCounter {
    fun insertTile(
        newTile: Tile,
        coordinate: Vec2,
    )

    fun setMeeple(
        m: Meeple,
        coord: TileCoordinate,
    )
}
