package controllers

import play.api._
import play.api.mvc._

import models._
import models.TileOwner._
import scala.util.Random
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
		    val game = Game(id, randomTiles, name, None, 0, 0, PlayerTurn.PlayerOne, Nil)
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
    val game = Game.fetch(id)
    if (game.isEmpty) 
      NotFound("Game " + id + " not found")
    else
      Ok(views.html.game(game.get))
  }
  
  def dbtest = Action {
	Ok("foo")
  }
  
  def submit(word: String, id: String, tiles: String) = Action {
    val game = Game.fetch(id)
    
    if (game.isEmpty)
      NotFound("Game " + id + " not found")
    else if (game.get.words.contains(word))
      Ok("PLAYED")
    else if (words.contains(word.toLowerCase())) {
      Game.submit(word, game.get, tiles)
      Ok("OK")
    } else
      Ok("FAIL")
  }
  
	val newgameForm = Form(
	  "name" -> nonEmptyText
	)
}