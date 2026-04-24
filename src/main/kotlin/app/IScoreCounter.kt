package app

/*
 * Score counter must count score and return meeple to players.
 */
interface IScoreCounter {
    // lastTile must be in the board
    fun countScore(
        lastCoord: Vec2,
        board: IGameBoardReadObject,
    ): Map<Color, Int>

    fun countFinalScore(): Map<Color, Int>
}
