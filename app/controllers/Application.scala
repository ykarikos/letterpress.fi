package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  
  val Alphabet = "ABCDEFGHIJKLMNOPRSTUVYÄÖ";
  
  def index = Action {
    Ok(views.html.index())
  }
  
  def game = Action {
    
    Ok(views.html.game())
  }
  
}