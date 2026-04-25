package app

interface IPlayerController {
    // Return the coordinate of the placed tile
    fun placeTile(): Vec2
}

class PlayerController : IPlayerController {
    override fun placeTile(): Vec2 {
        TODO("Need to implement.")
        return Vec2(0, 0)
    }
}
