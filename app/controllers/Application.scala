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
  
  val alphabet = List((0.11607, 'I'), (0.23011, 'A'), (0.31651, 'T'), (0.39098, 'S'), (0.45836, 'E'), 
		  (0.52488, 'U'), (0.59058, 'K'), (0.65494, 'N'), (0.71334, 'L'), (0.76748, 'O'), 
		  (0.80699, 'R'), (0.83889, 'Ä'), (0.86920, 'P'), (0.89892, 'M'), (0.92313, 'V'), 
		  (0.94481, 'H'), (0.96644, 'Y'), (0.97941, 'J'), (0.98683, 'D'), (0.99417, 'Ö'), 
		  (0.99682, 'G'), (0.99847, 'F'), (1.00000, 'B'))
  val rnd = Random
  val CURRENT = "current"
  
  def index = Action { request =>
    Ok(views.html.index(newgameForm, request.session.get(CURRENT)))
  }
  
  def newgame = Action { implicit request =>
    newgameForm.bindFromRequest.fold( 
        errors => BadRequest(views.html.index(newgameForm)),
        name => {
		    val id = randomId
		    val game = Game(id, randomTiles, name, None, (0, 0), PlayerTurn.PlayerOne, Nil)
		    Game.create(game)
		    Redirect(routes.Application.getgame(id)).withSession(CURRENT -> name)
		  }
        )
  }
  
  def randomId: String =
    rnd.alphanumeric.take(16).mkString
  
  def nextLetter: Char = {
    val r = rnd.nextDouble()
    alphabet.find(_._1 > r).get._2
  }
  
  def randomTiles: List[Tile] =
    for (i <- List.range(0,25)) 
    yield Tile(nextLetter, i, Neither)
  
  def getgame(id: String) = Action { request =>
    val name = request.session.get(CURRENT)
    val game = Game.fetch(id)
    if (game.isEmpty) 
      NotFound("Game " + id + " not found")
    else
      Ok(views.html.game(game.get, Game.score(game.get.tiles), Game.winner(game.get), name))
  }
  
  def joingame = Action { implicit request =>
    joinForm.bindFromRequest.fold(
        errors => BadRequest("FAIL"),
        { case (id, name) => {
		    val game = Game.fetch(id)
		    if (game.isEmpty) 
		      NotFound("Game " + id + " not found")
		    else if (game.get.playerTwo.isDefined)
		      Ok("FAIL")
		    else {
		      Game.join(id, name)
		      Ok("OK").withSession(CURRENT -> name)
		    }
          }
        }
     )
  }
  
  def pass = Action { implicit request =>
    idForm.bindFromRequest.fold(
        errors => BadRequest("FAIL"),
        { case (id) => {
        	val currentPlayer = session.get(CURRENT)
        	val game = Game.fetch(id)
        	
		    if (game.isEmpty)
		      NotFound("Game " + id + " not found")
		    else if (currentPlayer.isEmpty || !currentPlayer.equals(Game.getCurrentTurn(game.get)))
		      Ok("It's not your turn")
		    else {
		       Game.pass(game.get)
        	   Ok("OK")
		    }
        }
      }
    )
  }
  
  def getTurn(id: String) = Action {
	val game = Game.fetch(id)
	
    if (game.isEmpty)
      NotFound("Game " + id + " not found")
    else {
	  if (Game.ended(game.get.tiles))
	    Ok("0")
      else
    	Ok(game.get.turn.toString)
    }
  }
  
  def submit = Action { implicit request => 
    submitForm.bindFromRequest.fold(
        errors => BadRequest("FAIL"),
        { case (word, id, tiles) => {
            val currentPlayer = session.get(CURRENT)
		    val game = Game.fetch(id)

		    if (game.isEmpty)
		      NotFound("Game " + id + " not found")
		    else
		      Ok(Game.submit(game.get, word, tiles, currentPlayer))
        }
      }
    )
  }
  
  val idForm = Form(
      "id" -> nonEmptyText
  )
  
  val newgameForm = Form(
    "name" -> nonEmptyText
  )
	
  val joinForm = Form(tuple(
	"id" -> nonEmptyText,
	"name" -> nonEmptyText
  ))
	
  val submitForm = Form(tuple(
	"word" -> nonEmptyText,
	"id" -> nonEmptyText,
	"tiles" -> nonEmptyText
  ))
	    
}