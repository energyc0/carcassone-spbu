package app.context

import app.entities.GameObject
import app.entities.GameObjectDummy
import app.entities.GameObjectType
import app.entities.Meeple
import app.entities.Tile
import app.services.GameObjectFactory
import app.services.IGameRules
import app.utils.AreaCoordinate
import app.utils.TILE_AREA_SAMPLES
import app.utils.TileCoordinate
import app.utils.Vec2

data class TileCoordinateData(
    val tile: Tile,
    val objects: MutableMap<AreaCoordinate, GameObjectDummy>,
)

class GameBoard(
    private val gameRules: IGameRules,
) : IGameBoardReadWrite {
    private val gameObjects = mutableMapOf<Vec2, TileCoordinateData>()
    private val freeSpace = mutableSetOf<Vec2>(Vec2(0, 0))

    /**
     * Merge adjacent objects of the same type on the GameBoard and return a new one.
     * The given startCoord may not contain adjacent initialized values.
     */
    private fun mergeObjects(startCoord: TileCoordinate, objectType: GameObjectType,): GameObjectDummy {
        val resultObjectDummy = GameObjectFactory().createObject(objectType)
        gameObjects[startCoord.tileCoord]?.objects[startCoord.areaCoord] = resultObjectDummy
        /* There may be CROSSROAD, need to check */
        val resultObject = resultObjectDummy as? GameObject ?: return resultObjectDummy
        val visited = mutableSetOf<TileCoordinate>()
        val toVisit = ArrayDeque(listOf(startCoord))
        val objectSet = mutableSetOf(resultObject)
        val hasTile: (Vec2) -> Boolean = { gameObjects[it] != null }

        while (toVisit.isNotEmpty()) {
            val curCoord = toVisit.removeFirst()
            if (!hasTile(curCoord.tileCoord) || visited.contains(curCoord)) {
                continue
            }
            visited.add(curCoord)
            val curObject = getObject(curCoord)
            /* Check whether there is an object already.
             * Set a new one if there is empty or merge with existing.
             */
            if (curObject == null) {
                gameObjects[curCoord.tileCoord]?.objects[curCoord.areaCoord] = resultObject
            } else {
                if (curObject.type == objectType) {
                    // Merge and redefine curObject`s parent
                    if (!objectSet.contains(curObject)) {
                        objectSet.add(curObject)
                        resultObject.mergeWith(curObject)
                    }
                }
            }
            curCoord.getAdjacent().forEach { i ->
                if (!visited.contains(i)) {
                    val curType = getObjectType(i)
                    if (curType == objectType)
                        toVisit.addLast(i)
                }
            }
        }

        return resultObject
    }

    private fun addNewTile(
        newTile: Tile,
        coordinate: Vec2,
    ) {
        if (!freeSpace.contains(coordinate)) {
            throw IllegalArgumentException("There is no space to take.")
        }
        val tileObjects = mutableMapOf<AreaCoordinate, GameObjectDummy>()
        gameObjects[coordinate] = TileCoordinateData(newTile, tileObjects)

        // Add adjacent coordinates to freeSpace and delete the given coordinate
        coordinate.getAdjacent().forEach { adj -> if (!gameObjects.contains(adj)) freeSpace.add(adj) }
        freeSpace.remove(coordinate)


        for (x in 0..<TILE_AREA_SAMPLES) {
            for (y in 0..<TILE_AREA_SAMPLES) {
                val areaCoord = AreaCoordinate(x, y)
                val tileCoordinate = TileCoordinate(coordinate, areaCoord)
                val type = newTile.getTileArea(areaCoord)
                if (tileObjects[areaCoord] == null)
                    tileObjects[areaCoord] = mergeObjects(tileCoordinate, type)
            }
        }
    }

    private fun isValidPlacement(
        newTile: Tile,
        coordinate: Vec2,
    ): Boolean {
        val adjacentTiles = coordinate.getAdjacent().filter { gameObjects[it] != null }
        return (
            freeSpace.contains(coordinate) &&
                adjacentTiles.all { curCoord ->
                    val toTile =
                        gameObjects[curCoord]?.tile
                            ?: throw IllegalStateException("GameBoard cannot be empty.")
                    val dir = curCoord.getDirection(coordinate)
                    gameRules.canConnect(newTile, toTile, dir)
                }
        )
    }

    override fun setMeeple(
        m: Meeple,
        coord: TileCoordinate,
    ) {
        val objDummy = getObjectDummy(coord) ?: throw IllegalArgumentException("There is no tile.")
        if (objDummy.type == GameObjectType.CROSSROAD)
            throw IllegalArgumentException("Cannot set meeple to CROSSROAD")
        val obj = objDummy as GameObject
        if (obj.hasMeeple()) {
            throw IllegalArgumentException("Object already has meeple")
        }
        obj.addMeep(m)
        m.setArea(coord)
    }

    override fun insertTile(
        newTile: Tile,
        coordinate: Vec2,
    ) {
        // Edge case
        if (freeSpace.contains(Vec2(0, 0)) || isValidPlacement(newTile, coordinate)) {
            addNewTile(newTile, coordinate)
        } else {
            throw IllegalArgumentException("Cannot insert the tile there.")
        }
    }

    override fun getTile(coord: Vec2): Tile? = gameObjects[coord]?.tile

    override fun getFreeSpace(): List<Vec2> = freeSpace.toList()

    override fun getObject(coord: TileCoordinate): GameObject? = getObjectDummy(coord) as GameObject?

    override fun getObjectDummy(coord: TileCoordinate): GameObjectDummy? = gameObjects[coord.tileCoord]?.objects[coord.areaCoord]

    /** Get object type using tile */
    override fun getObjectType(coord: TileCoordinate): GameObjectType? = gameObjects[coord.tileCoord]?.tile?.getTileArea(coord.areaCoord)
}
