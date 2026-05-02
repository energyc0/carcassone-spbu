package integration

import app.context.GameBoard
import app.entities.Color
import app.entities.GameObjectType
import app.entities.Meeple
import app.entities.Tile
import app.entities.TileLook
import app.entities.TileLook.Companion.MID_SAMPLE
import app.services.GameObjectFactory
import app.services.GameRules
import app.services.ScoreCounter
import app.utils.AreaCoordinate
import app.utils.TILE_AREA_SAMPLES
import app.utils.TILE_AREA_SAMPLES_TOTAL
import app.utils.TileCoordinate
import app.utils.Vec2
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class ScoreCountIntegrationTest {
    private lateinit var board: GameBoard
    private lateinit var scoreCounter: ScoreCounter
    private lateinit var gameRules: GameRules
    private lateinit var objectFactory: GameObjectFactory

    @BeforeEach
    fun setUp() {
        gameRules = GameRules()
        board = GameBoard(gameRules)
        scoreCounter = ScoreCounter()
        objectFactory = GameObjectFactory()
    }

    private fun createArrayWithHorizontalRoad() : Array<GameObjectType> {
        return Array(TILE_AREA_SAMPLES_TOTAL) { i->
            val coord = AreaCoordinate(i % TILE_AREA_SAMPLES, i / TILE_AREA_SAMPLES)
            if (coord.y == MID_SAMPLE)
                GameObjectType.ROAD
            else
                GameObjectType.FIELD
        }
    }
    private fun createArrayWithHorizontalRoadCrossroad() : Array<GameObjectType>{
        val data = createArrayWithHorizontalRoad()
        data[MID_SAMPLE + MID_SAMPLE * TILE_AREA_SAMPLES] = GameObjectType.CROSSROAD
        return data
    }
    private fun createTileWithHorizontalRoad() : Tile {
        return Tile(TileLook(createArrayWithHorizontalRoad()))
    }
    private fun createTileWithHorizontalRoadCrossroad() : Tile {
        return Tile(TileLook(createArrayWithHorizontalRoadCrossroad()))
    }

    @Test
    @DisplayName("Simple road with 4 tiles should give 4 points")
    fun simpleRoadScoreTest() {
        val tile1 = createTileWithHorizontalRoad()
        val tile2 = createTileWithHorizontalRoad()
        val tileLeftCrossroad = createTileWithHorizontalRoadCrossroad()
        val tileRightCrossroad = createTileWithHorizontalRoadCrossroad()

        board.insertTile(tile1, Vec2(0, 0))
        board.insertTile(tileLeftCrossroad, Vec2(-1, 0))
        board.insertTile(tile2, Vec2(1, 0))
        val lastCoord = Vec2(2, 0)
        board.insertTile(tileRightCrossroad, lastCoord)

        val meeple = Meeple(Color.RED)
        val roadCoord = TileCoordinate(Vec2(0,0), AreaCoordinate(MID_SAMPLE, MID_SAMPLE))
        board.setMeeple(meeple, roadCoord)

        val scores = scoreCounter.countScore(lastCoord, board)

        assertEquals(4, board.getObject(roadCoord)?.tilesCountOccupied)
        assertEquals(4, scores[Color.RED])
    }
}