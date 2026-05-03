package app.context

import app.entities.Player
import app.gui.IGUIManager
import app.services.IGameRules
import app.services.IGameTilesLoader
import app.services.IPlayerController
import app.services.IPlayersInitializer
import app.services.IScoreCounter
import app.services.ITurnSuggester

/*
 * Main game class. Can throw IllegalStateArgumentException during initialization.
 */
class GameContext(
    players: Array<Player>,
    tilesLoader: IGameTilesLoader,
    gameRules: IGameRules,
) {
    private val gameState: IGameState = GameState(players)
    private val deck: Deck = Deck(tilesLoader.loadTiles())
    private val board: IGameBoardReadWrite = GameBoard(gameRules)

    fun gameplay(
        turnSuggester: ITurnSuggester,
        gui: IGUIManager,
        playerController: IPlayerController,
        scoreCounter: IScoreCounter,
    ) {
        while (deck.hasNextTile()) {
            val tile = deck.getNextTile()
            val variants = turnSuggester.suggestTurn(tile, board)

            if (variants.isEmpty()) {
                deck.tileSetBack(tile)
                continue
            }

            gameState.nextTurn()
            gui.drawUI()
            val tileCoord = playerController.placeTile()
            board.insertTile(tile, tileCoord)
            val score = scoreCounter.countScore(tileCoord, board)
            gameState.addPlayerScore(score)

            gui.drawTiles()
        }
    }
}
