package models

import models.TileOwner._
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Game(id: Long, tiles: List[Tile], playerOne: Player, playerTwo: Player)

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
   
}