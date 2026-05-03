package app.services

import app.context.TILES_DECK_COUNT
import app.entities.GameObjectType
import app.entities.Tile
import app.entities.TileLook
import app.utils.TILE_AREA_SAMPLES
import app.utils.TILE_AREA_SAMPLES_TOTAL
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

@Serializable
data class TileData(
    val id: String,
    val image: String,
    val tileAreas: String,
    val count: Int,
)

@Serializable
data class TileOptions(
    val startingTiles: Set<String>,
    val hasShield: Set<String>,
)

@Serializable
data class TileSet(
    val tiles: Array<TileData>,
    val tileOptions: TileOptions,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TileSet

        if (!tiles.contentEquals(other.tiles)) return false
        if (tileOptions != other.tileOptions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tiles.contentHashCode()
        result = 31 * result + tileOptions.hashCode()
        return result
    }
}

/*
 * Parse JSON file and get tiles.
 */
class GameTilesLoader : IGameTilesLoader {
    private companion object {
        const val TILES_PATH = "src/main/resources/Tiles.json"
    }

    private fun loadJsonString(filename: String): String? =
        try {
            File(filename).bufferedReader().use { reader ->
                reader.readText()
            }
        } catch (e: IOException) {
            println("IO Error reading file $filename: ${e.message}")
            null
        } catch (e: SecurityException) {
            println("Security Error accessing file $filename: ${e.message}")
            null
        }

    private fun parseJsonTile(
        tileData: TileData,
        tileOptions: TileOptions,
    ): Pair<Tile, Int> {
        if (tileData.tileAreas.length != TILE_AREA_SAMPLES_TOTAL + TILE_AREA_SAMPLES - 1) {
            throw IllegalStateException("Unexpected JSON data format for tile.")
        }

        val tileObjects =
            Array(TILE_AREA_SAMPLES_TOTAL) { i ->
                // Skip spaces
                when (tileData.tileAreas[i + i / TILE_AREA_SAMPLES]) {
                    'C' -> GameObjectType.CITY
                    'F' -> GameObjectType.FIELD
                    'M' -> GameObjectType.MONASTERY
                    'R' -> GameObjectType.ROAD
                    'X' -> GameObjectType.CROSSROAD
                    else -> throw IllegalStateException("Unexpected JSON data format for tile.")
                }
            }
        val tileLook = TileLook(tileObjects)
        val isStarting = tileOptions.startingTiles.contains(tileData.id)
        val tile = Tile(tileLook, isStarting)
        return Pair(tile, tileData.count)
    }

    override fun loadTiles(): MutableList<Tile> =
        try {
            val jsonStr = File(TILES_PATH).readText()
            val tileSet = Json.decodeFromString<TileSet>(jsonStr)

            val result = mutableListOf<Tile>()
            tileSet.tiles.forEach { tileData ->
                val (tile, count) = parseJsonTile(tileData, tileSet.tileOptions)
                repeat(count) { result.add(tile) }
            }

            require(result.size == TILES_DECK_COUNT) {
                "Expected $TILES_DECK_COUNT tiles, found ${result.size}"
            }

            result
        } catch (e: FileNotFoundException) {
            println("JSON file not found: ${e.message}")
            mutableListOf()
        } catch (e: IllegalArgumentException) {
            println("Validation error: ${e.message}")
            mutableListOf()
        } catch (e: IOException) {
            println("IO error reading file: ${e.message}")
            mutableListOf()
        }
}
