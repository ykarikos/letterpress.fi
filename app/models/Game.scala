package models

import models.TileOwner._
import TileOwner._
import com.mongodb.casbah.Imports._

object PlayerTurn extends Enumeration {
	type PlayerTurn = Value
	val PlayerOne = Value("1")
	val PlayerTwo = Value("2")
	
	def other(turn: PlayerTurn): PlayerTurn =
	  if (turn == PlayerOne)
	    PlayerTwo
	  else
	    PlayerOne
}

import PlayerTurn._

case class Game(id: String, tiles: List[Tile], playerOne: String, 
    playerTwo: Option[String], score: (Int, Int), 
    turn: PlayerTurn, words: List[Word])

case class Word(word: String, turn: PlayerTurn)
    
object Game {

  val mongoClient =  MongoClient()
  val mongoColl = mongoClient("letterpress")("game")

  def create(game: Game) { 
    val gameObj = MongoDBObject(
    	"id" -> game.id,
    	"tiles" -> serializeTiles(game.tiles),
    	"playerOne" -> game.playerOne,
    	"playerTwo" -> game.playerTwo,
    	"turn" -> game.turn.toString,
    	"words" -> List()
    )
    mongoColl += gameObj
  }
  
  def getCurrentTurn(game: Game) = 
    if (game.turn == PlayerTurn.PlayerOne)
      Some(game.playerOne)
    else
      game.playerTwo
  
  def updateTiles(id: String, turn: PlayerTurn, tiles: List[Tile]) {
    mongoColl.update(MongoDBObject("id" -> id), 
        $set(Seq("turn" -> turn.toString,
            "tiles" -> serializeTiles(tiles))))
  }
  
  def fetch(id: String): Option[Game] = {
    val game = mongoColl.findOne(MongoDBObject("id" -> id))
    if (game.isEmpty)
      None
    else {
      val gameVal = game.get
      val tiles = deserializeTiles(gameVal.getAsOrElse[String]("tiles", ""))
      Some(Game(gameVal.getAsOrElse[String]("id", null),
           tiles,
           gameVal.getAsOrElse[String]("playerOne", ""),
           gameVal.getAs[String]("playerTwo"),
           score(tiles),
           PlayerTurn.withName(gameVal.getAsOrElse[String]("turn", PlayerTurn.PlayerOne.toString)),
           deserializeWords(gameVal.as[MongoDBList]("words").toList collect { case s: String => s })
      ))
    }
  }

  def serializeWord(word: Word): String =
    word.turn.toString + word.word
      
  def deserializeWords(words: List[String]): List[Word] = {
    def deserializeWord(word: String): Word =
      Word(word.substring(1), PlayerTurn.withName(word.substring(0, 1)))
 
    if (words.isEmpty)
      Nil
    else
      deserializeWord(words.head) :: deserializeWords(words.tail)
  }
  
  def join(id: String, name: String) {
    mongoColl.update(MongoDBObject("id" -> id), 
        $set(Seq("playerTwo" -> name)))
  }

  def turn2Owner(turn: PlayerTurn): TileOwner =
    if (turn == PlayerTurn.PlayerOne)
      TileOwner.PlayerOne
    else
      TileOwner.PlayerTwo
  
  def submit(word: String, game: Game, tiles: String) {
	// save new word
    mongoColl.update(MongoDBObject("id" -> game.id),
        $push(Seq("words" -> serializeWord(Word(word, game.turn)))))
    
    val newTiles = Tile.normalize(Tile.selectWord(tiles.split(",").toList.map(s => s.toInt), game.tiles, game.turn))
    updateTiles(game.id, other(game.turn), newTiles)
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
  
  def score(tiles: List[Tile]): (Int, Int) = 
    (tiles.filter(t => locked(t.owner) == TileOwner.PlayerOneLocked).length,
    tiles.filter(t => locked(t.owner) == TileOwner.PlayerTwoLocked).length)
    
  def ended(tiles: List[Tile]): Boolean =
    tiles.count(_.owner == Neither) == 0

  def winner(game: Game): Option[String] =
    if (ended(game.tiles)) {
      val s = score(game.tiles)
      if (s._1 > s._2)
        Some(game.playerOne)
      else
        game.playerTwo
    } else
      None
}