package app.services

import app.entities.Tile

interface IGameTilesLoader {
    fun loadTiles(): MutableList<Tile>
}
