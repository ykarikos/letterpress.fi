package models

import models.TileOwner._
import TileOwner._
import com.mongodb.casbah.Imports._

object PlayerTurn extends Enumeration {
	type PlayerTurn = Value
	val PlayerOne = Value("1")
	val PlayerTwo = Value("2")
}

import PlayerTurn._

case class Game(id: String, tiles: List[Tile], playerOne: String, 
    playerTwo: Option[String], playerOneScore: Int, playerTwoScore: Int, 
    turn: PlayerTurn, words: List[String])

object Game {

  val mongoClient =  MongoClient()
  val mongoColl = mongoClient("letterpress")("game")

  def create(game: Game) { 
    val gameObj = MongoDBObject(
    	"id" -> game.id,
    	"tiles" -> serializeTiles(game.tiles),
    	"playerOne" -> game.playerOne,
    	"playerTwo" -> game.playerTwo,
    	"playerOneScore" -> game.playerOneScore,
    	"playerTwoScore" -> game.playerTwoScore,
    	"turn" -> game.turn.toString,
    	"words" -> game.words
    )
    mongoColl += gameObj
  }
  
  def updateScores(playerOneScore: Int, playerTwoScore: Int, id: String, turn: PlayerTurn, tiles: List[Tile]) {
    mongoColl.update(MongoDBObject("id" -> id), 
        $set(Seq("playerOneScore" -> playerOneScore,
            "playerTwoScore" -> playerTwoScore,
            "turn" -> turn.toString,
            "tiles" -> serializeTiles(tiles))))
  }
  
  def fetch(id: String): Option[Game] = {
    val game = mongoColl.findOne(MongoDBObject("id" -> id))
    if (game.isEmpty)
      None
    else {
      val gameVal = game.get
      Some(Game(gameVal.getAsOrElse[String]("id", null),
           deserializeTiles(gameVal.getAsOrElse[String]("tiles", "")),
           gameVal.getAsOrElse[String]("playerOne", ""),
           gameVal.getAs[String]("playerTwo"),
           gameVal.getAsOrElse[Int]("playerOneScore", 0),
           gameVal.getAsOrElse[Int]("playerTwoScore", 0),
           PlayerTurn.withName(gameVal.getAsOrElse[String]("turn", PlayerTurn.PlayerOne.toString)),
    	   gameVal.as[MongoDBList]("words").toList collect { case s: String => s }))
    }
  }
  

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
	// save new word
    mongoColl.update(MongoDBObject("id" -> game.id),
        $push(Seq("words" -> word)))
    
    val newTiles = setTileOwner(game.tiles, tiles.split(",").map(s => s.toInt), game.turn)
    
    if (game.turn == PlayerTurn.PlayerOne)
	  updateScores(game.playerOneScore + word.length(), game.playerTwoScore, 
	      game.id, PlayerTurn.PlayerTwo, newTiles)
	else
	  updateScores(game.playerOneScore, game.playerTwoScore + word.length(), 
	      game.id, PlayerTurn.PlayerOne, newTiles)
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
  
  def score(tiles: List[Tile]) = (0, 0)
}