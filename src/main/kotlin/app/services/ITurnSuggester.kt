package app.services

import app.context.IGameBoardReadTileSpace
import app.entities.Tile
import app.utils.Vec2

abstract class ITurnSuggester(
    private val gameRules: IGameRules,
) {
    // Suggest where to place the given tile
    abstract fun suggestTurn(
        tile: Tile,
        board: IGameBoardReadTileSpace,
    ): List<Vec2>
}
