package app

abstract class ITurnSuggester (private val gameRules: IGameRules) {
    /* Suggest where to place the given tile */
    abstract fun suggestTurn(tile: Tile,  board: GameBoard) : List<Coordinate>
}