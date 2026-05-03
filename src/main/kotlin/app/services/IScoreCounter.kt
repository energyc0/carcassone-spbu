package app.services

import app.context.IGameBoardRead
import app.entities.Color
import app.utils.Vec2

/*
 * Score counter must count score and return meeple to players.
 */
interface IScoreCounter {
    // lastTile must be in the board
    fun countScore(
        lastCoord: Vec2,
        board: IGameBoardRead,
    ): Map<Color, Int>

    fun countFinalScore(board: IGameBoardRead): Map<Color, Int>
}
