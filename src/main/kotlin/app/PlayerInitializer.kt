package app

class PlayersInitializer (gameRules: IGameRules, tilesLoader: IGameTilesLoader) : IPlayersInitializer {
    private fun collectPlayerInfo() : Player? {
        println("Write player's name: ")
        val name = readlnOrNull() ?: return null

        while (true) {
            println("Write player's color (RED, GREEN, BLUE, YELLOW, BLACK): ")
            val input = readlnOrNull() ?: return null

            try {
                val color = Color.valueOf(input.uppercase())
                return Player(color, name)
            } catch(e : IllegalArgumentException) {
                println("Invalid color type, please, try again.")
            }
        }
    }

    override fun collectPlayers() : Array<Player> {
        val playerList = mutableListOf<Player>()
        for (i in 1..PLAYER_COUNT) {
            val player = collectPlayerInfo() ?: break
            playerList.addLast(player)
        }

        require(playerList.size >= 2) { "There must be at least 2 players to start the game." }
        return playerList.toTypedArray()
    }
}