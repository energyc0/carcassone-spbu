package app.services

import app.context.PLAYER_COUNT
import app.entities.Color
import app.entities.Player

class PlayersInitializer : IPlayersInitializer {
    // Add new player name to set
    private fun collectPlayerName(
        playerNumber: Int,
        playerNames: MutableSet<String>,
    ) {
    }

    private fun collectPlayerInfo(
        playerNumber: Int,
        colorsReserved: MutableSet<Color>,
    ): Player? {
        println("Write player's №$playerNumber name or 'quit' to quit: ")
        val name = readlnOrNull() ?: return null
        if (name == "quit") {
            return null
        }
        var color: Color? = null

        while (color == null) {
            println("Write player's №$playerNumber color (RED, GREEN, BLUE, YELLOW, BLACK): ")
            val input = readlnOrNull() ?: break

            val converted = convertToColor(input) ?: continue
            if (colorsReserved.contains(converted)) {
                println("There is already a player with color $converted")
            } else {
                color = converted
                colorsReserved.add(converted)
            }
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
        val playerNames = mutableSetOf<String>()
        val colorsReserved = mutableSetOf<Color>()
        println("Enter players' information.")
        while (true) {
            while (playerList.size < MAX_PLAYERS_COUNT) {
                val player = collectPlayerInfo(playerList.size + 1, colorsReserved) ?: break
                println("Player name: ${player.name}")
                if (playerNames.contains(player.name)) {
                    println("There is already a player with name \"${player.name}\", please, try again.")
                } else {
                    playerList.addLast(player)
                    playerNames.add(player.name)
                }
            }
            if (playerList.size >= 2) {
                break
            }
            println("There must be at least 2 players to start the game. Try again? (Y/N)")
            val ans = readlnOrNull()
            if (ans?.uppercase() == "Y" || ans?.uppercase() == "YES") {
                playerList.clear()
                playerNames.clear()
            } else {
                return arrayOf()
            }
        }
        return playerList.toTypedArray()
    }
}
