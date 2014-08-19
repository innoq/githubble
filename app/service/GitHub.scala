package service

import play.api.libs.ws.WS
import play.api.Play.current
import play.api.libs.functional.syntax._
import play.api.libs.json._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger
import org.apache.http.HttpStatus


case class RateInfo (limit : Long, remaining : Long, reset :Long)

object RateInfo {  
  implicit val writes = Json.writes[RateInfo]
}

case class GitHubResource(json: Option[JsValue], success : Boolean,rateInfo : RateInfo) {

  def embedd(attribute: String): Future[GitHubResource] = {
    // There is no point in embedding into a filed Response
    if (!success) return Future.successful (this)
    Logger.info (s"Trying to embedd attribute $attribute");
    
    GitHub ((json.get \ attribute).as[String]).map {            
      attributeContent =>
        val transform = (__).json.update((__ \ attribute).json.put(attributeContent.json.get))
        copy(json =  json.map (_.transform(transform).get))
    }
  }
}

trait GitHubException extends Exception

case class GitHubForbidden(message : String, rateLimit : Int, rateLimitRest : Long	 ) extends Exception 

object GitHub {
  
  
  def apply (url : String ) : Future [GitHubResource] = {
    WS.url(url).get.map { response =>
      
      val rateInfo = RateInfo (
          response.header("X-RateLimit-Limit").map (_.toLong).getOrElse(-1),
          response.header("X-RateLimit-Remaining").map (_.toLong).getOrElse(-1),
          response.header("X-RateLimit-Reset").map (_.toLong).getOrElse(-1))
      
      
      response.status match {
        case HttpStatus.SC_FORBIDDEN => 
          //Logger.warn("GitHub returned 403")
          Future.failed(new RuntimeException ("Forbidden"))
          GitHubResource (None, false, rateInfo)
  
        case HttpStatus.SC_OK =>
          Logger.info ("Remaining Rate Limit: " + rateInfo.remaining)
          GitHubResource (Some (response.json), true, rateInfo)
      }
    }
  }
  
  
  def getUser(name: String) = GitHub (s"https://api.github.com/users/$name")
  
}
