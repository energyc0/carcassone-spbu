### Classes
See more in [carcassone.drawio](carcassone.drawio).

Entry point is **GameContext** class. It initializes **GameState**, **Deck** and creates **GameBoard**.
- **GameState** is a class that is responsible for containing players and choosing whose step is next.
- **Deck** is a class that is responsible for shuffling tiles and getting new tiles randomly.
- **GameBoard** is a class that is responsible for the current game board. You can either insert or get tiles from the board. The board is responsible for checking whether the given tile could be inserted to the given place (coordinates and type).
- **Player** is a class that is responsible for containing data about player (score, color, name, meeple).
- **Meeple** is a class that is contained in **Player**. Meeple can be set to a given **TileArea**.
- **Tile** is a class that is responsible for coordinate of the whole tile. Contains **TileLook** object.
- **TileLook** is a class that is responsible for rotation of the tile, for inner representation of the areas (**TileArea** for Meeple to set). TileLook object also contains data to draw for the GUI.
- **TileArea** abstract class is needed to implement the mechanics of the Meeple setting.
- **TileAreaScore** abstract class that is needed to implement different score counting mechanics.
- **TileAreaMonastery**, **TileAreaField**, **TileAreaRoad**, **TileAreaCrossRoad**, **TileAreaCity** extend the **TileArea** class by producing their own behaviour of counting score.
- **PlayerController** is a class that is responsible for implementing user input mechanics like placing a tile on the board or setting a meeple.
- **GUIManager** is a class that is responsible for user interface.
- **GameScore** class consists of **ScoreCounter** and **StatsSaver** classes.
- **ScoreCounter** class implements score count algorithms.
- **StatsSaver** class saves game information somewhere.
- **TurnSuggester** class suggests where to place a given tile in the game. It shows possible options for a player.

### Tile Representation

Tile is consists of TileArea's (you can see them in [carcassone.drawio](carcassone.drawio)). TileArea's form a graph. Vertices are connected if they are adjacent and have the same type.
It is convenient to use BFS to implement building some object (city, road, monastery) in the game and counting score.
When a player tries to set a tile to the board adjacent tiles are verified for having adjacent TileAreas of the same type.
Using TileArea's game can find meeple on an object.

### Game Cycle

GameContext is a main class. While the Deck is not empty GameContext gets a tile from the Deck. TurnSuggester checks whether there is a place to set the tile. If there is no the tile is got back to the Deck.
Then GameState decides whose step is next. A Player choose where to place the tile. User can set the tile only to the place where TurnSuggester said. The tile is set to the GameBoard. After it user may set a meeple to a tile on the GameBoard.
ScoreCounter count the score, GUIManager draws graphics. Start of the next step.
