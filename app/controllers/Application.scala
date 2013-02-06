package controllers

import play.api._
import play.api.mvc._

import models._
import models.TileOwner._
import scala.util.Random

object Application extends Controller {
  
  val alphabet = "ABCDEFGHIJKLMNOPRSTUVYÄÖ"
  val r = Random
  
  def index = Action {
    Ok(views.html.index())
  }
  
  def newgame(name: String) = Action {
    val tiles:List[Tile] = for (i <- List.range(0,25)) 
      yield Tile(alphabet.charAt(r.nextInt(alphabet.length())), i, Neither)
    val game = Game(1, tiles, Player(name), null)
    Ok(views.html.newgame(game))
  }
  
  def word(word: String) = Action {
    Ok("played word " + word)
  }
}