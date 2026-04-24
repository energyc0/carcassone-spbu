package app

/*
 * Merge two maps and sum up their values.
 */
fun mergeColorIntMaps(
    a: MutableMap<Color, Int>,
    b: MutableMap<Color, Int>,
): MutableMap<Color, Int> =
    (a.keys + b.keys)
        .associateWith { key ->
            a.getOrDefault(key, 0) + b.getOrDefault(key, 0)
        }.toMutableMap()

interface IGameBoardReadObject {
    fun getObject(coord: TileCoordinate): GameObject?
}

interface IGameBoardReadTile {
    fun getTile(coord: Vec2): Tile?

    fun getFreeSpace(): List<Vec2>
}

interface IGameBoardReadWrite :
    IGameBoardReadObject,
    IGameBoardReadTile {
    fun insertTile(
        newTile: Tile,
        coordinate: Vec2,
    )
}

data class TileCoordinateData(
    val tile: Tile,
    val objects: MutableMap<AreaCoordinate, GameObject>,
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
            gameObjects[startCoord.tileCoord]?.objects[startCoord.areaCoord]
                ?: throw IllegalStateException("Game object table corruption")
        val objectType = startObject.type
        var totalMeeple = startObject.meepleCount
        val visited = mutableSetOf(startCoord)
        val toVisit = Direction.entries.map { dir -> startCoord.getAdjacent(dir) }.toCollection(ArrayDeque())
        val objectSet = mutableSetOf(startObject)

        val resultObject = GameObjectFactory().createObject(objectType)
        while (toVisit.isNotEmpty()) {
            val curCoord = toVisit.removeFirst()
            visited.add(curCoord)
            val curObject = gameObjects[curCoord.tileCoord]?.objects[curCoord.areaCoord] ?: continue
            if (curObject.type == objectType) {
                if (!objectSet.contains(curObject)) {
                    objectSet.add(curObject)
                    totalMeeple = mergeColorIntMaps(totalMeeple, curObject.meepleCount)
                }
                curCoord.getAdjacent().forEach { i ->
                    if (!visited.contains(i)) {
                        toVisit.addLast(i)
                    }
                }
                gameObjects[curCoord.tileCoord]?.objects[curCoord.areaCoord] = resultObject
            }
        }

        resultObject.meepleCount = totalMeeple
        return resultObject
    }

    /*
     * Get the list of coordinates of existing adjacent tiles.
     */
    private fun getAdjacentTiles(coord: Vec2): List<Vec2> {
        val ret = mutableListOf<Vec2>()
        coord.getAdjacent().forEach { i -> if (gameObjects[i] != null) ret.add(i) }
        return ret.toList()
    }

    private fun takeSpace(cord: Vec2) {
        if (!freeSpace.contains(cord)) {
            throw IllegalArgumentException("There is no space to take.")
        }

        // Add adjacent coordinates to freeSpace and delete the given coordinate
        cord.getAdjacent().forEach { adj -> if (!gameObjects.contains(adj)) freeSpace.add(adj) }
        freeSpace.remove(cord)
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

    private fun tileAreaTypeToObject(type: TileAreaType): GameObjectType? =
        when (type) {
            TileAreaType.CITY -> GameObjectType.CITY
            TileAreaType.FIELD -> GameObjectType.FIELD
            TileAreaType.MONASTERY -> GameObjectType.MONASTERY
            TileAreaType.ROAD -> GameObjectType.ROAD
            TileAreaType.CROSSROAD -> null
        }

    private fun addNewTile(
        newTile: Tile,
        coordinate: Vec2,
    ) {
        takeSpace(coordinate)
        val tileObjects = mutableMapOf<AreaCoordinate, GameObject>()
        gameObjects[coordinate] = TileCoordinateData(newTile, tileObjects)
        for (x in 0..<TILE_AREA_SAMPLES) {
            for (y in 0..<TILE_AREA_SAMPLES) {
                val areaCoord = AreaCoordinate(x, y)
                val tileCoordinate = TileCoordinate(coordinate, areaCoord)
                val type = tileAreaTypeToObject(newTile.getTileArea(areaCoord)) ?: continue
                tileObjects[areaCoord] = findParentObject(type, tileCoordinate)
            }
        }
    }

    private fun isValidPlacement(
        newTile: Tile,
        coordinate: Vec2,
    ): Boolean {
        val adjacentTiles = getAdjacentTiles(coordinate)
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

    override fun getObject(coord: TileCoordinate): GameObject? = gameObjects[coord.tileCoord]?.objects[coord.areaCoord]
}
