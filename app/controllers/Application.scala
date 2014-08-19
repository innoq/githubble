package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json

object Application extends Controller {
  
  def home = Action { implicit request =>
    
    render {
      case Accepts.Html()  => Ok(views.html.index())
      case Accepts.Json()  => Ok(Json.obj())
    }
    
  }

}