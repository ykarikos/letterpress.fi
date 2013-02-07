package models

import models.TileOwner._
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Game(id: Long, tiles: List[Tile], playerOne: String, playerTwo: String)

object Game {
  def create(game: Game) {
    DB.withConnection { implicit c =>
      SQL("insert into game (tiles, playerOne, playerTwo) values " +
          "({tiles}, {playerOne}, {playerTwo})").on(
              'tiles -> serializeTiles(game.tiles)
              )
    }
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