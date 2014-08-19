package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json

object Application extends Controller {
  
  def home = Action { implicit request =>
    
    render {
      case Accepts.Html()  => Redirect(controllers.routes.Assets.at("index.html"))
      case Accepts.Json()  =>Ok (Json.obj())
    }
    
  }

}