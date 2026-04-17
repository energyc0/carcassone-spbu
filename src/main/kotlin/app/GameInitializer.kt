package app

class GameInitializer : IGameInitializer {
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

    fun collectPlayers() : Array<Player> {
        val playerList = mutableListOf<Player>()
        for (i in 1..PLAYER_COUNT) {
            val player = collectPlayerInfo() ?: break
            playerList.addLast(player)
        }

        return playerList.toTypedArray()
    }
}