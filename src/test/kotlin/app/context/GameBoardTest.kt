package app.context

import app.services.GameRules
import app.utils.AreaCoordinate
import app.utils.TileCoordinate
import app.utils.Vec2
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.awt.geom.Area
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class GameBoardTest {
    @Test
    @DisplayName("GameBoard initialization test")
    fun initTest() {
        val gameBoard = GameBoard(GameRules())
        assertNull(gameBoard.getTile(Vec2(0, 0)))
        assertNull(gameBoard.getTile(Vec2(12, 0)))
        assertNull(gameBoard.getTile(Vec2(0, 1)))
        assertNull(gameBoard.getObjectType(TileCoordinate(Vec2(0, 0), AreaCoordinate(0, 0))))
        assertNull(gameBoard.getObjectType(TileCoordinate(Vec2(0, 0), AreaCoordinate(1, 2))))
        assertNull(gameBoard.getObjectDummy(TileCoordinate(Vec2(0, 0), AreaCoordinate(0, 0))))
        assertNull(gameBoard.getObjectDummy(TileCoordinate(Vec2(0, 0), AreaCoordinate(1, 2))))
        assertNull(gameBoard.getObject(TileCoordinate(Vec2(0, 0), AreaCoordinate(0, 0))))
        assertNull(gameBoard.getObject(TileCoordinate(Vec2(1, 1), AreaCoordinate(0, 0))))
        assertNull(gameBoard.getObject(TileCoordinate(Vec2(0, 0), AreaCoordinate(1, 1))))

        assertEquals(List(1) { Vec2(0, 0) }, gameBoard.getFreeSpace())
    }
}
