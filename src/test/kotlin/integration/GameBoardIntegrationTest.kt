package integration

import app.context.GameBoard
import app.entities.Color
import app.entities.GameObjectType
import app.entities.Meeple
import app.entities.Player
import app.entities.Rotation
import app.entities.Tile
import app.entities.TileLook
import app.entities.TileLook.Companion.MID_SAMPLE
import app.services.Direction
import app.services.GameRules
import app.utils.AreaCoordinate
import app.utils.TILE_AREA_SAMPLES
import app.utils.TILE_AREA_SAMPLES_TOTAL
import app.utils.TileCoordinate
import app.utils.Vec2
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

internal class GameBoardIntegrationTest {
    private lateinit var gameBoard: GameBoard
    private lateinit var gameRules: GameRules

    private fun createArrayWithHorizontalRoad() : Array<GameObjectType> {
        return Array(TILE_AREA_SAMPLES_TOTAL) { i->
            val coord = AreaCoordinate(i % TILE_AREA_SAMPLES, i / TILE_AREA_SAMPLES)
            if (coord.y == MID_SAMPLE)
                GameObjectType.ROAD
            else
                GameObjectType.FIELD
        }
    }

    @BeforeEach
    fun setUp() {
        gameRules = GameRules()
        gameBoard = GameBoard(gameRules)
    }

    @Test
    @DisplayName("Should place starting tile and initialize board correctly")
    fun tilePlacementTest() {
        val areas = Array(TILE_AREA_SAMPLES_TOTAL) { GameObjectType.ROAD }
        val tileLook = TileLook(areas)
        val startTile = Tile(tileLook, true)

        gameBoard.insertTile(startTile, Vec2(0, 0))

        assertEquals(startTile, gameBoard.getTile(Vec2(0, 0)))

        val freeSpace = gameBoard.getFreeSpace()
        assertTrue(freeSpace.contains(Vec2(1, 0)))
        assertTrue(freeSpace.contains(Vec2(-1, 0)))
        assertTrue(freeSpace.contains(Vec2(0, 1)))
        assertTrue(freeSpace.contains(Vec2(0, -1)))
        assertEquals(4, freeSpace.size)
    }

    @Test
    @DisplayName("Should place multiple connected tiles and merge objects")
    fun multipleTilesPlacementTest() {
        val roadAreas = Array(TILE_AREA_SAMPLES_TOTAL) { GameObjectType.ROAD }
        val startTile = Tile(TileLook(roadAreas), true)
        gameBoard.insertTile(startTile, Vec2(0, 0))

        val secondTile = Tile(TileLook(roadAreas))

        gameBoard.insertTile(secondTile, Vec2(1, 0))

        assertEquals(startTile, gameBoard.getTile(Vec2(0, 0)))
        assertEquals(secondTile, gameBoard.getTile(Vec2(1, 0)))

        // Check whether objects has the same type
        var centerCoord = TileCoordinate(Vec2(0, 0), AreaCoordinate(1, 1))
        val obj1 = gameBoard.getObject(centerCoord)
        assertEquals(GameObjectType.ROAD, obj1?.type)

        centerCoord = TileCoordinate(Vec2(1, 0), AreaCoordinate(1, 1))
        val obj2 = gameBoard.getObject(centerCoord)
        assertEquals(GameObjectType.ROAD, obj2?.type)

        // Check whether it is the same object
        assertEquals(obj1, obj2)
    }

    @Test
    @DisplayName("Should not allow invalid tile placement")
    fun invalidPlacementTest() {
        val roadAreas = Array(TILE_AREA_SAMPLES_TOTAL) { GameObjectType.ROAD }
        val startTile = Tile(TileLook(roadAreas), true)
        gameBoard.insertTile(startTile, Vec2(0, 0))

        val cityAreas = Array(TILE_AREA_SAMPLES_TOTAL) { GameObjectType.CITY }
        val incompatibleTile = Tile(TileLook(cityAreas))

        assertThrows<IllegalArgumentException> {
            gameBoard.insertTile(incompatibleTile, Vec2(1, 0))
        }
    }

    @Test
    @DisplayName("Should correctly handle meeple placement on tile")
    fun meeplePlacementTest() {
        val roadAreas = Array(TILE_AREA_SAMPLES_TOTAL) { GameObjectType.ROAD }
        val startTile = Tile(TileLook(roadAreas), true)
        gameBoard.insertTile(startTile, Vec2(0, 0))

        val player = Player(Color.RED, "player1")
        val meeple = player.findFreeMeeple()
        assertNotNull(meeple)

        val tileCoord = TileCoordinate(Vec2(0, 0), AreaCoordinate(1, 1))

        gameBoard.setMeeple(meeple, tileCoord)

        val gameObject = gameBoard.getObject(tileCoord)
        assertNotNull(gameObject)
        assert(gameObject.hasMeeple())
    }

    @Test
    @DisplayName("Should not allow double meeple placement on same object")
    fun meepleDoublePlacementTest() {
        val roadAreas = Array(TILE_AREA_SAMPLES_TOTAL) { GameObjectType.ROAD }
        val startTile = Tile(TileLook(roadAreas), true)
        gameBoard.insertTile(startTile, Vec2(0, 0))

        val player1 = Player(Color.RED, "player1")
        val player2 = Player(Color.BLUE, "player2")
        val meeple1 = player1.findFreeMeeple()
        val meeple2 = player1.findFreeMeeple()
        val meeple3 = player2.findFreeMeeple()

        val tileCoord = TileCoordinate(Vec2(0, 0), AreaCoordinate(1, 1))

        assertNotNull(meeple1)
        assertNotNull(meeple2)
        assertNotNull(meeple3)
        gameBoard.setMeeple(meeple1, tileCoord)

        assertFailsWith<IllegalArgumentException> {
            gameBoard.setMeeple(meeple2, tileCoord)
        }
        assertFailsWith<IllegalArgumentException> {
            gameBoard.setMeeple(meeple3, tileCoord)
        }
    }

    @Test
    @DisplayName("Should handle tile rotation before placement")
    fun insertRotatedTileTest() {
        val startAreas = Array(TILE_AREA_SAMPLES_TOTAL) { GameObjectType.CITY }
        val startTile = Tile(TileLook(startAreas))
        gameBoard.insertTile(startTile, Vec2(0, 0))

        // Upper side of the tile is city
        val fieldCityAreas =
            Array(TILE_AREA_SAMPLES_TOTAL) { i ->
                if (i < TILE_AREA_SAMPLES * 2) {
                    GameObjectType.CITY
                } else {
                    GameObjectType.FIELD
                }
            }
        val tile2 = Tile(TileLook(fieldCityAreas))

        tile2.setRotation(Rotation.LEFT)
        assert(gameRules.canConnect(tile2, startTile, Direction.RIGHT))
        assertDoesNotThrow {
            gameBoard.insertTile(tile2, Vec2(1, 0))
        }

        val coord1 = TileCoordinate(Vec2(1, 0), AreaCoordinate(0, MID_SAMPLE))
        val coord2 = TileCoordinate(Vec2(0, 0), AreaCoordinate(MID_SAMPLE, MID_SAMPLE))
        assertEquals(tile2, gameBoard.getTile(coord1.tileCoord))
        assertEquals(gameBoard.getObject(coord1), gameBoard.getObject(coord2))
    }

    @Test
    @DisplayName("Should throw exception when placing tile outside free space")
    fun invalidFreeSpacePlacement() {
        val areas = Array(TILE_AREA_SAMPLES_TOTAL) { GameObjectType.FIELD }
        val tile = Tile(TileLook(areas))
        gameBoard.insertTile(tile, Vec2(0, 0))

        assertFailsWith<IllegalArgumentException> {
            gameBoard.insertTile(tile, Vec2(0, 0))
        }
        assertFailsWith<IllegalArgumentException> {
            gameBoard.insertTile(tile, Vec2(5, 5))
        }
    }

    @Test
    @DisplayName("Should correctly get object type at specific coordinate")
    fun getObjectTypeTest() {
        val areas =
            Array(TILE_AREA_SAMPLES_TOTAL) { index ->
                when (index) {
                    0 -> GameObjectType.CITY
                    TILE_AREA_SAMPLES_TOTAL - 1 -> GameObjectType.MONASTERY
                    else -> GameObjectType.FIELD
                }
            }

        val tile = Tile(TileLook(areas), true)
        gameBoard.insertTile(tile, Vec2(0, 0))

        val cityCoord = TileCoordinate(Vec2(0, 0), AreaCoordinate(0, 0))
        val monasteryCoord = TileCoordinate(Vec2(0, 0), AreaCoordinate(TILE_AREA_SAMPLES - 1, TILE_AREA_SAMPLES - 1))
        val fieldCoord = TileCoordinate(Vec2(0, 0), AreaCoordinate(1, 1))

        assertEquals(GameObjectType.CITY, gameBoard.getObjectType(cityCoord))
        assertEquals(GameObjectType.MONASTERY, gameBoard.getObjectType(monasteryCoord))
        assertEquals(GameObjectType.FIELD, gameBoard.getObjectType(fieldCoord))
    }

    @Test
    @DisplayName("Should handle sequence of tile placements forming a path")
    fun roadBuildingTest() {
        val roadAreas = createArrayWithHorizontalRoad()

        val positions =
            listOf(
                Vec2(0, 0),
                Vec2(1, 0),
                Vec2(2, 0),
                Vec2(3, 0),
            )

        positions.forEachIndexed { index, pos ->
            val tile =
                Tile(
                    isStarting = index == 0,
                    tileLook = TileLook(roadAreas),
                )
            gameBoard.insertTile(tile, pos)
        }

        positions.forEach { pos ->
            assertNotNull(gameBoard.getTile(pos))
        }

        val midAreaCoord = AreaCoordinate(MID_SAMPLE, MID_SAMPLE)
        val firstCoord = TileCoordinate(positions[0], midAreaCoord)
        val lastCoord = TileCoordinate(positions[3], midAreaCoord)

        val firstObject = gameBoard.getObject(firstCoord)
        val lastObject = gameBoard.getObject(lastCoord)

        assertEquals(firstObject, lastObject)
        assertNotNull(firstObject)
        firstObject.addMeep(Meeple(Color.RED))
        positions.forEach { pos ->
            val tilesOccupied = gameBoard.getObject(TileCoordinate(pos, midAreaCoord))?.tilesCountOccupied
            val curCoord = TileCoordinate(pos, midAreaCoord)
            assertEquals(positions.size, tilesOccupied)
            assert(gameBoard.getObject(curCoord)?.hasMeeple() == true)
        }
    }
}
