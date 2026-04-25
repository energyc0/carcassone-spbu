package app.context

import app.Deck
import app.GameBoard
import app.GameState
import app.IGUIManager
import app.IGameBoardReadWrite
import app.IGameRules
import app.IGameState
import app.IGameTilesLoader
import app.IPlayerController
import app.IPlayersInitializer
import app.IScoreCounter
import app.ITurnSuggester

class GameContext(
    playerInitializer: IPlayersInitializer,
    tilesLoader: IGameTilesLoader,
    gameRules: IGameRules,
) {
    private val gameState: IGameState
    private val deck: Deck
    private val board: IGameBoardReadWrite

    init {
        val players = playerInitializer.collectPlayers()
        gameState = GameState(players)
        deck = Deck(tilesLoader.loadTiles())
        board = GameBoard(gameRules)
    }

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