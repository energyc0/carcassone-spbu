package app

/*
 * Class is responsible for getting score for the
 * tile area.
 */
abstract class TileAreaScore (private val score: Int) {
    var hasGottenScore: Boolean = false
        private set

    fun getScore() : Int{
        hasGottenScore = true
        return score
    }
}