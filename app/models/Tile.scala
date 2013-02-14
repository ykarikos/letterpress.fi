package models

object TileOwner extends Enumeration {
	type TileOwner = Value
	val Neither = Value("0")
	val PlayerOne = Value("1")
	val PlayerTwo = Value("2")
	val PlayerOneLocked = Value("3")
	val PlayerTwoLocked = Value("4")
	
	def locked(o: TileOwner): TileOwner =
	  o match {
	  case Neither => Neither
	  case PlayerOne => PlayerOneLocked
	  case PlayerOneLocked => PlayerOneLocked
	  case PlayerTwo => PlayerTwoLocked
	  case PlayerTwoLocked => PlayerTwoLocked
	}
	
	def isLocked(o: TileOwner): Boolean =
	  if (o == PlayerOneLocked || o == PlayerTwoLocked)
	    true
	  else
	    false  
}

import TileOwner._
import PlayerTurn._

case class Tile(letter: Char, id: Int, owner: TileOwner)

object Tile {
  val ROWS = 5
  val COLS = 5
  
  def lock(id: Int, tiles: List[Tile]): Boolean = 
    false
    
  def normalize(tiles: List[Tile]): List[Tile] = 
    tiles map { t => normalize(neighbors(tiles, t), t) }
    
  def normalize(neighbors: List[Tile], tile: Tile): Tile =
    tile
    
  def neighbors(tiles: List[Tile], tile: Tile): List[Tile] = {
    val col = tile.id % ROWS
    val row = tile.id / COLS
    List((col-1, row), (col+1, row), (col, row-1), (col, row+1)).
    	filter { c => c._1 >= 0 && c._2 >= 0 && c._1 < COLS && c._2 < ROWS }.
    	map { c => tiles(c._2 * 5 + c._1) }
  }
    
  def selectWord(word: List[Int], tiles: List[Tile], player: PlayerTurn): List[Tile] = 
    if (word.isEmpty)
      tiles
    else
      selectWord(word.tail, tiles map changeTile(player, word.head), player)
  
  def changeTile(player: PlayerTurn, id: Int)(t: Tile): Tile =
    if (t.id != id)
      t
    else { 
	    val turn = Game.turn2Owner(player)
	    if (turn == t.owner || TileOwner.isLocked(t.owner))
	      t
	    else
	      Tile(t.letter, t.id, turn)
    }
     
      
  
}