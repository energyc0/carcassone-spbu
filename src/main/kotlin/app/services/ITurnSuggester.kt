package app.services

import app.context.IGameBoardRead
import app.entities.Tile
import app.utils.Vec2

abstract class ITurnSuggester(
    protected val gameRules: IGameRules,
) {
    // Suggest where to place the given tile
    abstract fun suggestTurn(
        tile: Tile,
        board: IGameBoardRead,
    ): List<Vec2>
}
