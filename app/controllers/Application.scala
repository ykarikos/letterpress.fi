package controllers

import play.api._
import play.api.mvc._

import models._
import scala.util.Random

object Application extends Controller {
  
  val alphabet = "ABCDEFGHIJKLMNOPRSTUVYÄÖ"
  val r = Random
  
  def index = Action {
    Ok(views.html.index())
  }
  
  def game = Action {
    val tiles:List[Tile] = for (i <- List.range(0,25)) 
      yield Tile(alphabet.charAt(r.nextInt(alphabet.length())), i)
    val board = Board(tiles)
    Ok(views.html.game(board))
  }
  
}