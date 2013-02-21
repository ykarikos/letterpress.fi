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
  val alphabet = List((0.11607, 'I'), (0.23011, 'A'), (0.31651, 'T'), (0.39098, 'S'), (0.45836, 'E'), 
		  (0.52488, 'U'), (0.59058, 'K'), (0.65494, 'N'), (0.71334, 'L'), (0.76748, 'O'), 
		  (0.80699, 'R'), (0.83889, 'Ä'), (0.86920, 'P'), (0.89892, 'M'), (0.92313, 'V'), 
		  (0.94481, 'H'), (0.96644, 'Y'), (0.97941, 'J'), (0.98683, 'D'), (0.99417, 'Ö'), 
		  (0.99682, 'G'), (0.99847, 'F'), (1.00000, 'B'))
  val rnd = Random
  
  def index = Action {
    Ok(views.html.index(newgameForm))
  }
  
  def newgame = Action { implicit request =>
    newgameForm.bindFromRequest.fold( 
        errors => BadRequest(views.html.index(newgameForm)),
        name => {
		    val id = randomId
		    val game = Game(id, randomTiles, name, None, (0, 0), PlayerTurn.PlayerOne, Nil)
		    Game.create(game)
		    Redirect(routes.Application.getgame(id))
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
  
  def getgame(id: String) = Action {
    val game = Game.fetch(id)
    if (game.isEmpty) 
      NotFound("Game " + id + " not found")
    else
      Ok(views.html.game(game.get, Game.score(game.get.tiles), Game.winner(game.get)))
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
		      Ok("OK")
		    }
          }
        }
     )
  }
  // (word: String, id: String, tiles: String)
  def submit = Action { implicit request => 
    submitForm.bindFromRequest.fold(
        errors => BadRequest("FAIL"),
        { case (word, id, tiles) => {
		    val game = Game.fetch(id)
		    
		    if (game.isEmpty)
		      NotFound("Game " + id + " not found")
		    else if (game.get.playerTwo.isEmpty && game.get.turn == PlayerTurn.PlayerTwo)
		      Ok("PLAYERMISSING")
		    else if (Game.ended(game.get.tiles))
		      Ok("ENDED")
		    else if (game.get.words.count(_.word == word) > 0)
		      Ok("PLAYED")
		    else if (words.contains(word.toLowerCase())) {
		      Game.submit(word, game.get, tiles)
		      Ok("OK")
		    } else
		      Ok("FAIL")
        }
      }
    )
  }
  
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