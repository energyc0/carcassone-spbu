package app

class GameContext(
    playerInitializer: IPlayersInitializer,
    tilesLoader: IGameTilesLoader,
    gameRules: IGameRules,
) {
    private val gameState: IGameState
    private val deck: Deck
    private val board: GameBoard

    init {
        val players = playerInitializer.collectPlayers()
        gameState = GameState(players)
        deck = Deck(tilesLoader.loadTiles())
        board = GameBoard(gameRules)
    }

    fun gameplay(turnSuggester: ITurnSuggester, gui : IGUIManager, playerController: IPlayerController, scoreCounter : IScoreCounter) {
        while (deck.hasNextTile()) {
            val tile = deck.getNextTile()
            val variants = turnSuggester.suggestTurn(tile, board)

            if (variants.isEmpty()) {
                deck.tileSetBack(tile)
                continue
            }

            gameState.nextTurn()
            gui.drawUI()
            playerController.processInput()
            val score = scoreCounter.countScore()

            gui.drawTiles()
        }
    }
}
