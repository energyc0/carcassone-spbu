package app

/*
 * Score counter must count score and return meeple to players.
 */
interface IScoreCounter {
    /* lastTile must be in the board */
    fun countScore(lastTile : Tile, board: GameBoard) : Map<Color, Int>
    fun countFinalScore() : Map<Color, Int>
}