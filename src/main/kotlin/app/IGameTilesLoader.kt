package app

interface IGameTilesLoader {
    fun loadTiles() : MutableList<Tile>
}