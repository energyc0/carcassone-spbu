package app.services

import app.context.TILES_DECK_COUNT
import app.entities.GameObjectType
import app.entities.Tile
import app.entities.TileLook
import app.utils.TILE_AREA_SAMPLES
import app.utils.TILE_AREA_SAMPLES_TOTAL
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class TileData(
    val id: String,
    val image: String,
    val tile_areas: String,
    val count: Int,
)

@Serializable
data class TileOptions(
    val starting_tiles: Set<String>,
    val has_shield: Set<String>,
)

@Serializable
data class TileSet(
    val tiles: Array<TileData>,
    val tile_options: TileOptions,
)

/*
 * Parse JSON file and get tiles.
 */
class GameTilesLoader : IGameTilesLoader {
    private companion object {
        const val TILES_PATH = "src/main/resources/Tiles.json"
    }

    private fun loadJsonString(filename: String): String? {
        try {
            return File(filename).readText()
        } catch (e: Exception) {
            println("${e.message}")
            return null
        }
    }

    private fun parseJsonTile(
        tileData: TileData,
        tileOptions: TileOptions,
    ): Pair<Tile, Int> {
        if (tileData.tile_areas.length != TILE_AREA_SAMPLES_TOTAL + TILE_AREA_SAMPLES - 1) {
            throw Exception("Unexpected JSON data format for tile.")
        }

        val tileObjects =
            Array(TILE_AREA_SAMPLES_TOTAL) { i ->
                // Skip spaces
                when (tileData.tile_areas[i + i / TILE_AREA_SAMPLES]) {
                    'C' -> GameObjectType.CITY
                    'F' -> GameObjectType.FIELD
                    'M' -> GameObjectType.MONASTERY
                    'R' -> GameObjectType.ROAD
                    'X' -> GameObjectType.CROSSROAD
                    else -> throw Exception("Unexpected JSON data format for tile.")
                }
            }
        val tileLook = TileLook(tileObjects)
        val isStarting = tileOptions.starting_tiles.contains(tileData.id)
        val tile = Tile(tileLook, isStarting)
        return Pair(tile, tileData.count)
    }

    override fun loadTiles(): MutableList<Tile> {
        try {
            val jsonStr = File(TILES_PATH).readText()

            val tileSet = Json.decodeFromString<TileSet>(jsonStr)

            val result = mutableListOf<Tile>()
            tileSet.tiles.forEach { i ->
                val (tile, count) = parseJsonTile(i, tileSet.tile_options)
                repeat(count) { result.addLast(tile) }
            }

            check(result.size == TILES_DECK_COUNT) { "Expected $TILES_DECK_COUNT in the JSON file, found ${result.size}." }
            return result
        } catch (e: Exception) {
            println("${e.message}")
            return mutableListOf()
        }
    }
}
