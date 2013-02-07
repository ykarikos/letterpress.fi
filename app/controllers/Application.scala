package controllers

import play.api._
import play.api.mvc._

import models._
import models.TileOwner._
import scala.util.Random
import anorm._ 
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

object Application extends Controller {
  
  val alphabet = "ABCDEFGHIJKLMNOPRSTUVYÄÖ"
  val r = Random
  
  def index = Action {
    Ok(views.html.index())
  }
  
  def newgame(name: String) = Action {
    val owners = TileOwner.values.toList
    val tiles:List[Tile] = for (i <- List.range(0,25)) 
      yield Tile(alphabet.charAt(r.nextInt(alphabet.length())), i, Neither)
    val game = Game(1, tiles, name, "")
    Game.create(game)
    Ok(views.html.newgame(game))
  }
  
  def getgame(id: Long) = Action {
    val game = Game.fetch(id)
    Ok(views.html.newgame(game))
  }
  
  def dbtest = Action {
	DB.withConnection { implicit c =>
	//val result: Long = SQL("select count(*) from game").as(scalar[Long].single)
	//val result: Int = SQL("delete from game").executeUpdate()
	val result: Long = SQL("select id from game limit 1").as(scalar[Long].single)
	  Ok("result: " + result)
	}
  }
  
  def word(word: String) = Action {
    Ok("played word " + word)
  }
}