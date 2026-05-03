package app.services

import app.context.PLAYER_COUNT
import app.entities.Color
import app.entities.Player

class PlayersInitializer : IPlayersInitializer {
    private fun collectPlayerInfo(
        playerNumber: Int,
        colorsReserved: MutableSet<Color>,
    ): Player? {
        println("Write player's №$playerNumber name or 'quit' to quit: ")
        val name = readlnOrNull()
        if (name == null || name == "quit") {
            return null
        }
        var color: Color? = null

        while (color == null) {
            println("Write player's №$playerNumber color (RED, GREEN, BLUE, YELLOW, BLACK): ")
            val input = readlnOrNull()
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

    private fun convertToColor(input: String?): Color? =
        try {
            if (input == null) {
                null
            } else {
                Color.valueOf(input.uppercase())
            }
        } catch (e: IllegalArgumentException) {
            println("Invalid color type: \"${e.message}\"\nPlease, try again.")
            null
        }

    override fun collectPlayers(): Array<Player> {
        val playerList = mutableListOf<Player>()
        val playerNames = mutableSetOf<String>()
        val colorsReserved = mutableSetOf<Color>()
        println("Enter players' information.")
        while (playerList.size < 2) {
            collectPlayersBatch(playerList, playerNames, colorsReserved)

            if (playerList.size >= 2) {
                break
            }

            if (!askToRetry()) {
                return arrayOf()
            }

            playerList.clear()
            playerNames.clear()
            colorsReserved.clear()
        }

        return playerList.toTypedArray()
    }

    private fun collectPlayersBatch(
        playerList: MutableList<Player>,
        playerNames: MutableSet<String>,
        colorsReserved: MutableSet<Color>,
    ) {
        while (playerList.size < MAX_PLAYERS_COUNT) {
            val player =
                collectPlayerInfo(playerList.size + 1, colorsReserved)
                    ?: break

            println("Player name: ${player.name}")

            if (playerNames.contains(player.name)) {
                println("There is already a player with name \"${player.name}\", please, try again.")
            } else {
                playerList.add(player)
                playerNames.add(player.name)
            }
        }
    }

    private fun askToRetry(): Boolean {
        println("There must be at least 2 players to start the game. Try again? (Y/N)")
        val ans = readlnOrNull()
        return ans?.uppercase() == "Y" || ans?.uppercase() == "YES"
    }
}
