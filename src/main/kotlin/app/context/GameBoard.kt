package app.context

import app.entities.GameObject
import app.entities.GameObjectDummy
import app.entities.GameObjectFactory
import app.entities.GameObjectType
import app.entities.Meeple
import app.entities.Tile
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

    /*
     * Merge adjacent objects of the same type on the GameBoard and return a new one.
     */
    private fun mergeObjects(startCoord: TileCoordinate): GameObject {
        val startObject =
            (
                gameObjects[startCoord.tileCoord]?.objects[startCoord.areaCoord]
                    ?: throw IllegalStateException("Game object table corruption")
            ) as GameObject
        val objectType = startObject.type
        val totalMeeple = startObject.meeple
        val visited = mutableSetOf<TileCoordinate>()
        val toVisit = ArrayDeque(listOf(startCoord))
        val objectSet = mutableSetOf(startObject)

        val resultObject = GameObjectFactory().createObject(objectType)
        while (toVisit.isNotEmpty()) {
            val curCoord = toVisit.removeFirst()
            visited.add(curCoord)
            val curObject = (gameObjects[curCoord.tileCoord]?.objects[curCoord.areaCoord] ?: continue) as GameObject
            if (curObject.type == objectType) {
                if (!objectSet.contains(curObject)) {
                    objectSet.add(curObject)
                    totalMeeple.addAll(curObject.meeple)
                }
                curCoord.getAdjacent().forEach { i ->
                    if (!visited.contains(i)) {
                        toVisit.addLast(i)
                    }
                }
                gameObjects[curCoord.tileCoord]?.objects[curCoord.areaCoord] = resultObject
            }
        }

        resultObject.meeple = totalMeeple
        return resultObject
    }

    /*
     * Try to find an adjacent GameObject and merge with it or return a new one.
     */
    private fun findParentObject(
        type: GameObjectType,
        coord: TileCoordinate,
    ): GameObject {
        val adj =
            coord
                .getAdjacent()
                .mapNotNull { curCoord ->
                    val obj = getObject(curCoord)
                    val t = obj?.type
                    if (t == type) {
                        obj
                    } else {
                        null
                    }
                }.toSet()

        if (adj.size != 1) {
            gameObjects[coord.tileCoord]?.objects[coord.areaCoord] = GameObjectFactory().createObject(type)
            return mergeObjects(coord)
        }
        return adj.last()
    }

    private fun addNewTile(
        newTile: Tile,
        coordinate: Vec2,
    ) {
        val tileObjects = mutableMapOf<AreaCoordinate, GameObjectDummy>()
        gameObjects[coordinate] = TileCoordinateData(newTile, tileObjects)
        if (!freeSpace.contains(coordinate)) {
            throw IllegalArgumentException("There is no space to take.")
        }

        // Add adjacent coordinates to freeSpace and delete the given coordinate
        coordinate.getAdjacent().forEach { adj -> if (!gameObjects.contains(adj)) freeSpace.add(adj) }
        freeSpace.remove(coordinate)

        for (x in 0..<TILE_AREA_SAMPLES) {
            for (y in 0..<TILE_AREA_SAMPLES) {
                val areaCoord = AreaCoordinate(x, y)
                val tileCoordinate = TileCoordinate(coordinate, areaCoord)
                val type = newTile.getTileArea(areaCoord)
                tileObjects[areaCoord] = findParentObject(type, tileCoordinate)
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
                        gameObjects[coordinate]?.tile
                            ?: throw IllegalStateException("GameBoard cannot be empty.")
                    val dir = coordinate.getDirection(curCoord)
                    gameRules.canConnect(newTile, toTile, dir)
                }
        )
    }

    override fun setMeeple(
        m: Meeple,
        coord: TileCoordinate,
    ) {
        val obj = getObject(coord) ?: throw IllegalArgumentException("There is no tile.")
        if (obj.hasMeeple()) {
            throw IllegalArgumentException("Object already has meeple")
        }
        obj.addMeep(m)
    }

    override fun insertTile(
        newTile: Tile,
        coordinate: Vec2,
    ) {
        // Edge case
        if (freeSpace.contains(Vec2(0, 0))) {
            addNewTile(newTile, coordinate)
            return
        }

        if (!isValidPlacement(newTile, coordinate)) {
            throw IllegalArgumentException("Cannot insert the tile there.")
        }

        addNewTile(newTile, coordinate)
    }

    override fun getTile(coord: Vec2): Tile? = gameObjects[coord]?.tile

    override fun getFreeSpace(): List<Vec2> = freeSpace.toList()

    override fun getObject(coord: TileCoordinate): GameObject? = getObjectDummy(coord) as GameObject?

    override fun getObjectDummy(coord: TileCoordinate): GameObjectDummy? = gameObjects[coord.tileCoord]?.objects[coord.areaCoord]

    override fun getObjectType(coord: TileCoordinate): GameObjectType? = getObjectDummy(coord)?.type
}
