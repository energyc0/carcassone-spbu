package app

/*
 * Controls free space in GameBoard class.
 * Improves algorithms efficiency.
 */
class FreeSpace {
    private val freeSpace = mutableSetOf(Coordinate(0,0))
    private val takenSpace = mutableSetOf<Coordinate>()

    fun hasSpace(cord: Coordinate) : Boolean {
        return freeSpace.contains(cord)
    }

    fun takeSpace(cord: Coordinate) {
        if (!hasSpace(cord))
            throw IllegalArgumentException("There is no space to take.")

        /* Add adjacent coordinates to freeSpace and delete the given coordinate */
        cord.getAdjacent().forEach { adj -> if (!takenSpace.contains(adj)) freeSpace.add(adj) }
        takenSpace.add(cord)
        freeSpace.remove(cord)
    }

    fun getSpace() : List<Coordinate> = freeSpace.toList()
}