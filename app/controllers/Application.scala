package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json
import play.api.http.ContentTypeOf
import play.api.libs.json.JsValue
import play.api.http.Writeable

object Application extends Controller {

  // Content Type Magic to be moved in helper
  def JSON_API(implicit codec: Codec) = withCharset("application/vnd.api+json")
  
  implicit def contentTypeOf_JsValue(implicit codec: Codec): ContentTypeOf[JsValue] = {
    ContentTypeOf[JsValue](Some(JSON_API))
  }

  implicit def writeableOf_JsValue(implicit codec: Codec): Writeable[JsValue] = {
    Writeable(jsval => codec.encode(jsval.toString))
  }
	
 
  def home = Action {
    Ok (Json.obj())
  }

}