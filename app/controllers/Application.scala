package controllers

import http.CustomContentType._
import play.api._
import play.api.mvc._
import play.api.libs.json.Json

object Application extends Controller {
  val AccpetingApiJson = Accepting("application/vnd.api+json")
  def home = Action { implicit request =>
    
    render {
      case Accepts.Html()  => Redirect(controllers.routes.Assets.at("index.html"))
      case AccpetingApiJson()  =>Ok (Json.obj())
    }
    
  }

}