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
import play.api.data._
import play.api.data.Forms._

object Application extends Controller {
  
  val alphabet = "ABCDEFGHIJKLMNOPRSTUVYÄÖ"
  val r = Random
  
  def index = Action {
    Ok(views.html.index(newgameForm))
  }
  
  def newgame = Action { implicit request =>
    newgameForm.bindFromRequest.fold( 
        errors => BadRequest(views.html.index(newgameForm)),
        name => {
		    val id = randomId
		    val game = Game(id, randomTiles, name, "", 0, 0)
		    Game.create(game)
		    Redirect(routes.Application.getgame(id))
		  }
        )
  }
  
  def randomId: String =
    r.alphanumeric.take(16).mkString
  
  def randomTiles: List[Tile] = 
    for (i <- List.range(0,25)) 
      yield Tile(alphabet.charAt(r.nextInt(alphabet.length())), i, Neither)
  
  def getgame(id: String) = Action {
    Ok(views.html.game(Game.fetch(id)))
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
  
	val newgameForm = Form(
	  "name" -> nonEmptyText
	)
}