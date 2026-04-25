package app.entities

/*
 * TileArea is an abstract part of a whole tile.
 * It is used to constitute an object in the game,
 * to set game figures there and to count game scores.
 * TileArea's are indexed from top left to bottom right using AreaCoordinate class.
 */
abstract class TileArea(
    val type: GameObjectType,
)
