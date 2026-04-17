package app

class GameContext (playerInitializer: IPlayersInitializer, tilesLoader: IGameTilesLoader, gameRules: IGameRules){
    private val gameState: IGameState
    private val deck: Deck
    private val board: GameBoard

    init {
        val players = playerInitializer.collectPlayers()
        gameState = GameState(players)
        deck = Deck(tilesLoader.loadTiles())
        board = GameBoard(gameRules)
    }

    fun nextTurn() {
        TODO("Not implemented.")
    }
}