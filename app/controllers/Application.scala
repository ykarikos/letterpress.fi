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
  
  val words = scala.io.Source.fromFile("conf/wordlist/fi.txt", "UTF-8").getLines.toVector
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
		    val game = Game(id, randomTiles, name, None, 0, 0, PlayerTurn.PlayerOne)
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
	SQL("update game set playerOneScore=playerOneScore+5").executeUpdate()
	val result: Int = SQL("select playerOneScore from game limit 1").as(scalar[Int].single)
	  Ok("result: " + result)
	}
  }
  
  def submit(word: String, id: String, tiles: String) = Action {
    // TODO check played words for this game
    if (words.contains(word.toLowerCase())) {
      Game.submit(word, id, tiles)
      Ok("OK")
    } else
      Ok("FAIL")
  }
  
	val newgameForm = Form(
	  "name" -> nonEmptyText
	)
}