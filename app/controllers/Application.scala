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
      yield Tile(alphabet.charAt(r.nextInt(alphabet.length())), i, randomOwner)
    val board = Board(tiles)
    Ok(views.html.game(board))
  }
  
  def randomOwner: String = {
    r.nextInt(10) match {
      case 1 => "red"
      case 2 => "blue"
      case 3 => "redLocked"
      case 4 => "blueLocked"
      case _ => ""
    }
  }
  
}