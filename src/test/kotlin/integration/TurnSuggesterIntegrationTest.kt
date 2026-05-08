package integration

import app.context.GameBoard
import app.entities.GameObjectType
import app.entities.Tile
import app.entities.TileLook
import app.entities.TileLook.Companion.MID_SAMPLE
import app.services.GameRules
import app.services.TurnSuggester
import app.utils.AreaCoordinate
import app.utils.TILE_AREA_SAMPLES
import app.utils.TILE_AREA_SAMPLES_TOTAL
import app.utils.Vec2
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class TurnSuggesterIntegrationTest {
    private lateinit var gameBoard: GameBoard
    private lateinit var gameRules: GameRules
    private lateinit var turnSuggester: TurnSuggester

    @BeforeEach
    fun setUp() {
        gameRules = GameRules()
        gameBoard = GameBoard(gameRules)
        turnSuggester = TurnSuggester(gameRules)
    }

    // Helper functions to create different tile types
    private fun createHorizontalRoadTile(isStarting: Boolean = false): Tile {
        val areas =
            Array(TILE_AREA_SAMPLES_TOTAL) { i ->
                val coord = AreaCoordinate(i % TILE_AREA_SAMPLES, i / TILE_AREA_SAMPLES)
                if (coord.y == MID_SAMPLE) {
                    GameObjectType.ROAD
                } else {
                    GameObjectType.FIELD
                }
            }
        return Tile(TileLook(areas), isStarting)
    }

    private fun createVerticalRoadTile(isStarting: Boolean = false): Tile {
        val areas =
            Array(TILE_AREA_SAMPLES_TOTAL) { i ->
                val coord = AreaCoordinate(i % TILE_AREA_SAMPLES, i / TILE_AREA_SAMPLES)
                if (coord.x == MID_SAMPLE) {
                    GameObjectType.ROAD
                } else {
                    GameObjectType.FIELD
                }
            }
        return Tile(TileLook(areas), isStarting)
    }

    private fun createCityTile(isStarting: Boolean = false): Tile {
        val areas =
            Array(TILE_AREA_SAMPLES_TOTAL) { i ->
                val coord = AreaCoordinate(i % TILE_AREA_SAMPLES, i / TILE_AREA_SAMPLES)
                if (coord.y < MID_SAMPLE && 0 < coord.x && coord.x < TILE_AREA_SAMPLES - 1) {
                    GameObjectType.CITY
                } else {
                    GameObjectType.FIELD
                }
            }
        return Tile(TileLook(areas), isStarting)
    }

    private fun createFieldTile(isStarting: Boolean = false): Tile {
        val areas = Array(TILE_AREA_SAMPLES_TOTAL) { GameObjectType.FIELD }
        return Tile(TileLook(areas), isStarting)
    }

    /** Road from middle up to middle right */
    private fun createCornerRoadTile(isStarting: Boolean = false): Tile {
        val areas =
            Array(TILE_AREA_SAMPLES_TOTAL) { i ->
                val coord = AreaCoordinate(i % TILE_AREA_SAMPLES, i / TILE_AREA_SAMPLES)
                when {
                    coord.x == MID_SAMPLE && coord.y <= MID_SAMPLE -> GameObjectType.ROAD
                    coord.y == MID_SAMPLE && coord.x >= MID_SAMPLE -> GameObjectType.ROAD
                    else -> GameObjectType.FIELD
                }
            }
        return Tile(TileLook(areas), isStarting)
    }

    private fun createCrossroadTile(isStarting: Boolean = false): Tile {
        val areas =
            Array(TILE_AREA_SAMPLES_TOTAL) { i ->
                val coord = AreaCoordinate(i % TILE_AREA_SAMPLES, i / TILE_AREA_SAMPLES)
                when {
                    coord.x == MID_SAMPLE || coord.y == MID_SAMPLE -> GameObjectType.ROAD
                    else -> GameObjectType.FIELD
                }
            }
        areas[MID_SAMPLE + MID_SAMPLE * TILE_AREA_SAMPLES] = GameObjectType.CROSSROAD
        return Tile(TileLook(areas), isStarting)
    }

    @Test
    @DisplayName("Starting tile should have exactly one suggested position (0,0)")
    fun suggestTurnForStartingTileTest() {
        val startTile = createHorizontalRoadTile(isStarting = true)

        val suggestions = turnSuggester.suggestTurn(startTile, gameBoard)

        assertEquals(1, suggestions.size)
        assertEquals(Vec2(0, 0), suggestions[0])
    }

    @Test
    @DisplayName("Should suggest positions adjacent to existing tiles when connection possible")
    fun suggestTurnWithSingleAdjacentTest() {
        val startTile = createHorizontalRoadTile(isStarting = true)
        gameBoard.insertTile(startTile, Vec2(0, 0))

        val roadTile = createHorizontalRoadTile()

        val suggestions = turnSuggester.suggestTurn(roadTile, gameBoard)

        assertEquals(4, suggestions.size)
        assertTrue(suggestions.contains(Vec2(1, 0)))
        assertTrue(suggestions.contains(Vec2(-1, 0)))
        assertTrue(suggestions.contains(Vec2(0, 1)))
        assertTrue(suggestions.contains(Vec2(0, -1)))
    }

    @Test
    @DisplayName("Should only suggest positions where tile can connect to adjacent tiles")
    fun suggestTurnOnlyValidConnectionsTest() {
        val startTile = createCityTile(isStarting = true)
        gameBoard.insertTile(startTile, Vec2(0, 0))

        val roadTile = createHorizontalRoadTile()
        val suggestions = turnSuggester.suggestTurn(roadTile, gameBoard)
        assertTrue(suggestions.contains(Vec2(1, 0)))
        assertTrue(suggestions.contains(Vec2(-1, 0)))
        assertTrue(suggestions.contains(Vec2(0, -1)))
        assertEquals(3, suggestions.size)
    }

    @Test
    @DisplayName("Should suggest multiple positions when multiple connections possible")
    fun suggestTurnMultiplePositionsTest() {
        val startTile = createHorizontalRoadTile(isStarting = true)
        gameBoard.insertTile(startTile, Vec2(0, 0))

        val rightTile = createHorizontalRoadTile()
        gameBoard.insertTile(rightTile, Vec2(1, 0))

        val connectingTile = createHorizontalRoadTile()

        val suggestions = turnSuggester.suggestTurn(connectingTile, gameBoard)

        val expected = gameBoard.getFreeSpace().toSet()
        assertEquals(expected, suggestions.toSet())
    }

    @Test
    @DisplayName("Should return empty list when tile cannot be placed anywhere")
    fun suggestTurnNoValidPositionsTest() {
        val startTile = createFieldTile(isStarting = true)
        gameBoard.insertTile(startTile, Vec2(0, 0))

        val areas = Array(TILE_AREA_SAMPLES_TOTAL) { GameObjectType.CITY }
        val incompatibleTile = Tile(TileLook(areas))

        val suggestions = turnSuggester.suggestTurn(incompatibleTile, gameBoard)
        assert(suggestions.isEmpty())
    }

    @Test
    @DisplayName("Should handle L-shaped road connections correctly")
    fun suggestTurnCornerRoadTest() {
        val startTile = createHorizontalRoadTile(isStarting = true)
        gameBoard.insertTile(startTile, Vec2(0, 0))
        val cornerTile = createCornerRoadTile()
        val suggestions = turnSuggester.suggestTurn(cornerTile, gameBoard)
        assertTrue(suggestions.contains(Vec2(1, 0)))
        assertTrue(suggestions.contains(Vec2(-1, 0)))
        assertTrue(suggestions.contains(Vec2(0, -1)))
        assertTrue(suggestions.contains(Vec2(0, 1)))
        assertEquals(4, suggestions.size)
    }

    @Test
    @DisplayName("Should handle crossroad tile connections correctly")
    fun suggestTurnCrossroadTest() {
        val startTile = createHorizontalRoadTile(isStarting = true)
        gameBoard.insertTile(startTile, Vec2(0, 0))

        val crossroadTile = createCrossroadTile()

        val suggestions = turnSuggester.suggestTurn(crossroadTile, gameBoard)

        assertTrue(suggestions.contains(Vec2(1, 0)))
        assertTrue(suggestions.contains(Vec2(-1, 0)))
        assertEquals(2, suggestions.size)
    }

    @Test
    @DisplayName("Should handle tile placement near monastery")
    fun suggestTurnNearMonasteryTest() {
        fun createMonasteryTile(): Tile {
            val areas =
                Array(TILE_AREA_SAMPLES_TOTAL) { i ->
                    val coord = AreaCoordinate(i % TILE_AREA_SAMPLES, i / TILE_AREA_SAMPLES)
                    if (coord == AreaCoordinate(MID_SAMPLE, MID_SAMPLE)) {
                        GameObjectType.MONASTERY
                    } else {
                        GameObjectType.FIELD
                    }
                }
            return Tile(TileLook(areas), true)
        }

        val monasteryTile = createMonasteryTile()
        gameBoard.insertTile(monasteryTile, Vec2(0, 0))

        val fieldTile = createFieldTile()

        val suggestions = turnSuggester.suggestTurn(fieldTile, gameBoard)

        assertEquals(4, suggestions.size)
        assertTrue(
            suggestions.containsAll(
                listOf(
                    Vec2(1, 0),
                    Vec2(-1, 0),
                    Vec2(0, 1),
                    Vec2(0, -1),
                ),
            ),
        )
    }

    @Test
    @DisplayName("Should handle large board with holes correctly")
    fun suggestTurnWithHolesTest() {
        val fieldTile = createFieldTile(isStarting = true)
        gameBoard.insertTile(fieldTile, Vec2(0, 0))

        val ringPositions =
            listOf(
                Vec2(1, 0),
                Vec2(2, 0),
                Vec2(2, 1),
                Vec2(2, 2),
                Vec2(1, 2),
                Vec2(0, 2),
                Vec2(-1, 2),
                Vec2(-1, 1),
                Vec2(-1, 0),
                Vec2(-1, -1),
                Vec2(0, -1),
                Vec2(1, -1),
                Vec2(2, -1),
            )

        ringPositions.forEach { pos ->
            gameBoard.insertTile(createFieldTile(), pos)
        }

        val holePositions = listOf(Vec2(0, 1), Vec2(1, 1))
        assertTrue(gameBoard.getFreeSpace().containsAll(holePositions))

        val connectingTile = createFieldTile()

        val suggestions = turnSuggester.suggestTurn(connectingTile, gameBoard)

        assertTrue(suggestions.containsAll(holePositions))
    }
}
