package models

import models.TileOwner._
import TileOwner._

object PlayerTurn extends Enumeration {
	type PlayerTurn = Value
	val PlayerOne = Value("1")
	val PlayerTwo = Value("2")
}

import PlayerTurn._

case class Game(id: String, tiles: List[Tile], playerOne: String, 
    playerTwo: Option[String], playerOneScore: Int, playerTwoScore: Int, 
    turn: PlayerTurn)

object Game {
  
  def create(game: Game) { }
  
  def updateScores(playerOneAdd: Int, playerTwoAdd: Int, id: String, turn: PlayerTurn, tiles: List[Tile]) {
    /*
    DB.withConnection { implicit c =>
      SQL("update game set playerOneScore=playerOneScore+{playerOneAdd}, "+
          "playerTwoScore=playerTwoScore+{playerTwoAdd}, " +
          "turn={turn}, tiles={tiles} where id={id}").on(
              'playerOneAdd -> playerOneAdd,
              'playerTwoAdd -> playerTwoAdd,
              'turn -> turn.toString,
              'tiles -> serializeTiles(tiles),
              'id -> id
              ).executeUpdate()
      }
      * 
      */
  }
  
  def fetch(id: String): Option[Game] = None
  

  def turn2Owner(turn: PlayerTurn): TileOwner =
    if (turn == PlayerTurn.PlayerOne)
      TileOwner.PlayerOne
    else
      TileOwner.PlayerTwo
  
  def setTileOwner(tiles: List[Tile], tilesToChange: Array[Int], turn: PlayerTurn): List[Tile] =
    if (tiles.isEmpty)
      Nil
    else {
      val head: Tile = tiles.head
      if (tilesToChange.contains(head.id))
        Tile(head.letter, head.id, turn2Owner(turn)) :: setTileOwner(tiles.tail, tilesToChange, turn)
      else
        head :: setTileOwner(tiles.tail, tilesToChange, turn)
    }
      
  
  def submit(word: String, game: Game, tiles: String) {
    // TODO: Save played words
    val newTiles = setTileOwner(game.tiles, tiles.split(",").map(s => s.toInt), game.turn)
    
    if (game.turn == PlayerTurn.PlayerOne)
	  updateScores(word.length(), 0, game.id, PlayerTurn.PlayerTwo, newTiles)
	else
	  updateScores(0, word.length(), game.id, PlayerTurn.PlayerOne, newTiles)
  } 
  
  def serializeTiles(tiles: List[Tile]) =
   (for (t <- tiles) yield (t.letter + t.owner.toString)).mkString
  
   def deserializeTiles(tiles: String): List[Tile] = {
    def deserializeTiles0(i: Int, tiles: String): List[Tile] =
      if (tiles.length() < 2)
        Nil
      else {
        val c = tiles.charAt(0)
        val owner = TileOwner.withName(tiles.substring(1, 2))	
        Tile(c, i, owner) :: deserializeTiles0(i + 1, tiles.substring(2))
    }
    deserializeTiles0(0, tiles)
  }
}