package app

data class Coordinate(val x: Int, val y: Int)

class Tile (val isStarting: Boolean, private val tileLook: TileLook){
    var coords: Coordinate? = null
        private set

    fun setTile(coord: Coordinate) {
        require(coords == null) { "Tile is already set. "}
        coords = coord
    }

    fun setMeeple(meeple: Meeple, tileAreaDir: TileAreaDir) {
        tileLook.setMeeple(meeple, tileAreaDir)
    }
}