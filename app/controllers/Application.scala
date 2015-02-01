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
    Ok(views.html.index(newgameForm, setnameForm, request.session.get(CURRENT)))
  }
  
  def newgame = Action { implicit request =>
    newgameForm.bindFromRequest.fold( 
        errors => BadRequest(views.html.index(newgameForm, setnameForm)),
        { case (name, size) => {
		    val id = randomId
		    val game = Game(id, randomTiles(size*size), name, None, (0, 0), PlayerTurn.PlayerOne, Nil, size)
		    Game.create(game)
		    Redirect(routes.Application.getgame(id)).withSession(CURRENT -> name)
		  }
        }
    )
  }
  
  def setname = Action { implicit request =>
    setnameForm.bindFromRequest.fold( 
        errors => BadRequest(views.html.index(newgameForm, setnameForm)),
        name =>	Redirect(routes.Application.listgames).withSession(CURRENT -> name)
    )
  }
  
  def randomId: String =
    rnd.alphanumeric.take(16).mkString
  
  def nextLetter: Char = {
    val r = rnd.nextDouble()
    alphabet.find(_._1 > r).get._2
  }
  
  def randomTiles(count: Int): List[Tile] =
    for (i <- List.range(0, count)) 
    yield Tile(nextLetter, i, Neither)
  
  def getgame(id: String) = Action { request =>
    val name = request.session.get(CURRENT)
    Game.fetch(id) match {
      case None =>
      	NotFound("Game " + id + " not found")
      case Some(game) =>
        name match {
          case None => 
        	Ok(views.html.game(game, Game.score(game.tiles), Game.winner(game), name))
          case Some(currentName) => 
        	Ok(views.html.game(game, Game.score(game.tiles), Game.winner(game), name)).withSession(CURRENT -> currentName)
        }
        
    }
  }
  
  def listgames = Action { request =>
    val currentPlayer = request.session.get(CURRENT)
    Ok(views.html.list(setnameForm, currentPlayer, Game.listGames(currentPlayer)))
  }
    
  def joingame = Action { implicit request =>
    joinForm.bindFromRequest.fold(
        errors => BadRequest("FAIL"),
        { case (id, name) => {
		    Game.fetch(id) match {
		      case None =>
		      	NotFound("Game " + id + " not found")
		      case Some(game) =>
			    if (game.playerTwo.isDefined)
			      Ok("FAIL")
			    else {
			      Game.join(id, name)
			      Ok("OK").withSession(CURRENT -> name)
			    }
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
        	Game.fetch(id) match {
        	  case None =>
        	  	NotFound("Game " + id + " not found")
        	  	
        	  case Some(game) =>
			    if (currentPlayer.isEmpty || !currentPlayer.equals(Game.getCurrentTurn(game)))
			      Ok("It's not your turn")
			    else {
			       Game.pass(game)
	        	   Ok("OK")
			    }
        	}
        }
      }
    )
  }
  
  def getTurn(id: String) = Action {
	Game.fetch(id) match {
	  case Some(game) =>
		  if (Game.ended(game.tiles))
		    Ok("0")
	      else
	    	Ok(game.turn.toString)
	  case None =>
		  NotFound("Game " + id + " not found")
	}
  }
  
  def submit = Action { implicit request => 
    submitForm.bindFromRequest.fold(
        errors => BadRequest("FAIL"),
        { case (word, id, tiles) => {
            val currentPlayer = session.get(CURRENT)
		    Game.fetch(id) match {
              case None => NotFound("Game " + id + " not found")
              case Some(game) => Ok(Game.submit(game, word, tiles, currentPlayer))
            }
        }
      }
    )
  }
  
  val idForm = Form(
      "id" -> nonEmptyText
  )
  
  val newgameForm = Form(tuple(
    "name" -> nonEmptyText,
    "size" -> number 
  ))
	
  val setnameForm = Form(
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