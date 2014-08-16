package controllers

import http.CustomContentType._
import play.api._
import play.api.mvc._
import play.api.libs.json.Json

object Application extends Controller {

  def home = Action {
    Ok (Json.obj())
  }

}