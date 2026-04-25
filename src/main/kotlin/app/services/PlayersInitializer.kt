package app

class PlayersInitializer : IPlayersInitializer {
    private fun collectPlayerInfo(): Player? {
        println("Write player's name: ")
        val name = readlnOrNull() ?: return null
        var color: Color? = null

        while (color == null) {
            println("Write player's color (RED, GREEN, BLUE, YELLOW, BLACK): ")
            val input = readlnOrNull() ?: break

            color = convertToColor(input)
        }
        return if (color != null) Player(color, name) else null
    }

    private fun convertToColor(input: String): Color? =
        try {
            Color.valueOf(input.uppercase())
        } catch (e: IllegalArgumentException) {
            println("Invalid color type: \"${e.message}\"\nPlease, try again.")
            null
        }

    override fun collectPlayers(): Array<Player> {
        val playerList = mutableListOf<Player>()
        for (i in 1..PLAYER_COUNT) {
            val player = collectPlayerInfo() ?: break
            playerList.addLast(player)
        }

        require(playerList.size >= 2) { "There must be at least 2 players to start the game." }
        return playerList.toTypedArray()
    }
}
