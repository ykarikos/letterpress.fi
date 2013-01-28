package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Game(id: Long, tiles: List[Tile], playerOne: Player, playerTwo: Player)

object Game {
  /*
  def create(tiles: List[Tile], playerOne: Player, playerTwo: Player) {
    DB.withConnection { implicit c =>
      SQL("insert into game (tiles, playerOne, playerTwo) values " +
          "({tiles}, {playerOne}, {playerTwo})").on(
              'tiles -> tiles
              )
    }
  }
  */
}