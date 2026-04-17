package app
import kotlin.random.Random
import kotlin.random.nextInt

fun main() {
    val player = Player(Color.RED, "Maxim")
    println(player.score)
    player.addScore(11)
    println(player.score)
    println(player.name)
    println(player.color.toString())
    val values = mutableListOf<Int>()
    for (i in 1..5) {
        values.add(Random.nextInt(0, 100))
    }
    println(values)
    println(values.shuffled())
}
