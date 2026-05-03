package integration

import app.context.GameBoard
import app.entities.Color
import app.entities.GameObjectMonastery.Companion.MONASTERY_TOTAL_SCORE
import app.entities.GameObjectType
import app.entities.Meeple
import app.entities.Rotation
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

    private fun createMonasteryField() : Array<GameObjectType> {
        return Array(TILE_AREA_SAMPLES_TOTAL) { i->
            val coord = AreaCoordinate(i % TILE_AREA_SAMPLES, i / TILE_AREA_SAMPLES)
            if (coord == AreaCoordinate(MID_SAMPLE, MID_SAMPLE))
                GameObjectType.MONASTERY
            else
                GameObjectType.FIELD
        }
    }

    private fun createMonasteryTile() : Tile {
        return Tile(TileLook(createMonasteryField()))
    }

    private fun createField() : Array<GameObjectType> {
        return Array(TILE_AREA_SAMPLES_TOTAL) { GameObjectType.FIELD }
    }

    private fun createFieldTile() : Tile {
        return Tile(TileLook(createField()))
    }

    private fun createCity() : Array<GameObjectType> {
        return Array(TILE_AREA_SAMPLES_TOTAL) { i ->
            val coord = AreaCoordinate(i % TILE_AREA_SAMPLES, i / TILE_AREA_SAMPLES)
            if (coord.y < MID_SAMPLE && 0 < coord.x && coord.x < TILE_AREA_SAMPLES-1) GameObjectType.CITY
            else GameObjectType.FIELD
        }
    }

    private fun createCityTile() : Tile {
        return Tile(TileLook(createCity()))
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
    @DisplayName("Simple road with 4 tiles should give 4 points and do not give more")
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
        assertEquals(1, scores.size)

        val doubleScores = scoreCounter.countScore(lastCoord, board)
        assert(doubleScores.isEmpty())
    }

    @Test
    @DisplayName("Simple road with 2 tiles should give 0 points in score and 2 points in final score")
    fun simpleRoadFinalScoreTest() {
        val tile1 = createTileWithHorizontalRoad()
        val tile2 = createTileWithHorizontalRoad()

        board.insertTile(tile1, Vec2(0, 0))
        val lastCoord = Vec2(1, 0)
        board.insertTile(tile2, lastCoord)

        val meeple = Meeple(Color.RED)
        val roadCoord = TileCoordinate(Vec2(0,0), AreaCoordinate(MID_SAMPLE, MID_SAMPLE))
        board.setMeeple(meeple, roadCoord)

        val emptyScores = scoreCounter.countScore(lastCoord, board)
        assert(emptyScores.isEmpty())
        val scores = scoreCounter.countFinalScore(board)

        assertEquals(2, board.getObject(roadCoord)?.tilesCountOccupied)
        assertEquals(2, scores[Color.RED])
        assertEquals(1, scores.size)
    }

    @Test
    @DisplayName("Completed monastery should give 9 points")
    fun completedMonasteryTest() {
        val monasteryTile = createMonasteryTile()

        board.insertTile(monasteryTile, Vec2(0, 0))

        val coordToInsert = arrayOf(
            Vec2(0,1),
            Vec2(0,-1),
            Vec2(1,0),
            Vec2(-1,0),
            Vec2(1,1),
            Vec2(1,-1),
            Vec2(-1,1),
            Vec2(-1,-1))
        for (v in coordToInsert) {
            board.insertTile(createFieldTile(), v)
        }


        val monasteryCoord = TileCoordinate(Vec2(0, 0), AreaCoordinate(MID_SAMPLE, MID_SAMPLE))
        val meeple = Meeple(Color.GREEN)
        board.setMeeple(meeple, monasteryCoord)

        val scores = scoreCounter.countScore(Vec2(0, 0), board)

        assertEquals(MONASTERY_TOTAL_SCORE, scores[Color.GREEN])
    }

    @Test
    @DisplayName("Simple road with 4 tiles should give 4 points for two players and do not give more")
    fun simpleTwoRoadScoreTest() {
        val tile1 = createTileWithHorizontalRoad()
        val tile2 = createTileWithHorizontalRoad()
        val tileLeftCrossroad = createTileWithHorizontalRoadCrossroad()
        val tileRightCrossroad = createTileWithHorizontalRoadCrossroad()

        /* Start tile */
        board.insertTile(tile1, Vec2(0, 0))
        /* Insert field tiles above */
        for (x in arrayOf(0, -1, 1, 2)) {
            board.insertTile(createFieldTile(), Vec2(x, 1))
        }
        /* Insert crossroad and set tile */
        board.insertTile(tileLeftCrossroad, Vec2(-1, 0))
        val roadCoord1 = TileCoordinate(Vec2(0,0), AreaCoordinate(MID_SAMPLE+1, MID_SAMPLE))
        val meepleRed = Meeple(Color.RED)
        board.setMeeple(meepleRed, roadCoord1)

        /* Set second player meeple and connect roads */
        board.insertTile(tileRightCrossroad, Vec2(2, 0))
        val roadCoord2 = TileCoordinate(Vec2(2, 0), AreaCoordinate(MID_SAMPLE-1, MID_SAMPLE))
        val meepleBlue = Meeple(Color.BLUE)
        board.setMeeple(meepleBlue, roadCoord2)
        val lastCoord = Vec2(1,0)
        board.insertTile(tile2, lastCoord)

        val scores = scoreCounter.countScore(lastCoord, board)

        assertEquals(4, board.getObject(roadCoord1)?.tilesCountOccupied)
        assertEquals(4, board.getObject(roadCoord2)?.tilesCountOccupied)
        assertEquals(2, scores.size)
        assertEquals(4, scores[Color.RED])
        assertEquals(4, scores[Color.BLUE])

        val doubleScores = scoreCounter.countScore(lastCoord, board)
        assert(doubleScores.isEmpty())
    }

    @Test
    @DisplayName("Field gives 3 points for each adjacent completed city")
    fun fieldScoreTest() {
        val fieldTile = createFieldTile()
        val straightCityTile = createCityTile()
        val flippedCityTile = createCityTile()
        flippedCityTile.setRotation(Rotation.FLIPPED)

        board.insertTile(straightCityTile, Vec2(0, 0))
        assert(scoreCounter.countScore(Vec2(0, 0), board).isEmpty())
        board.insertTile(flippedCityTile, Vec2(0, 1))
        assert(scoreCounter.countScore(Vec2(0, 1), board).isEmpty())
        board.insertTile(fieldTile, Vec2(0, 2))

        val fieldCoord = TileCoordinate(Vec2(0,2), AreaCoordinate(0,0))
        val fieldMeeple = Meeple(Color.BLUE)
        board.setMeeple(fieldMeeple, fieldCoord)

        val finalScores = scoreCounter.countFinalScore(board)
        assertEquals(1, finalScores.size)
        assertEquals(3, finalScores[Color.BLUE])
        assert(scoreCounter.countScore(Vec2(0,2), board).isEmpty())
       //val finalScores = scoreCounter.countFinalScore(board)
        assertEquals(1, finalScores.size)
        assertEquals(3, finalScores[Color.BLUE])
    }
}