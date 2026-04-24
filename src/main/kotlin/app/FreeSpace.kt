package app

/*
 * Controls free space in GameBoard class.
 * Improves algorithms efficiency.
 */
class FreeSpace {
    private val freeSpace = mutableSetOf(Vec2(0, 0))
    private val takenSpace = mutableSetOf<Vec2>()

    fun hasSpace(cord: Vec2): Boolean = freeSpace.contains(cord)

    fun takeSpace(cord: Vec2) {
        if (!hasSpace(cord)) {
            throw IllegalArgumentException("There is no space to take.")
        }

        // Add adjacent coordinates to freeSpace and delete the given coordinate
        cord.getAdjacent().forEach { adj -> if (!takenSpace.contains(adj)) freeSpace.add(adj) }
        takenSpace.add(cord)
        freeSpace.remove(cord)
    }

    fun getSpace(): List<Vec2> = freeSpace.toList()
}
