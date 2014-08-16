package http

import play.api.mvc.Codec
import play.api.libs.json.Json
import play.api.http.ContentTypeOf
import play.api.libs.json.JsValue
import play.api.http.Writeable


import play.api.http.ContentTypes

object CustomContentType {
  def JSON_API(implicit codec: Codec) =ContentTypes.withCharset("application/vnd.api+json")
  
  implicit def contentTypeOf_JsValue(implicit codec: Codec): ContentTypeOf[JsValue] = {
    ContentTypeOf[JsValue](Some(JSON_API))
  }

  implicit def writeableOf_JsValue(implicit codec: Codec): Writeable[JsValue] = {
    Writeable(jsval => codec.encode(jsval.toString))
  }

}