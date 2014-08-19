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
case class GitHubResource(json: JsValue, success : Boolean,rateInfo : RateInfo) {

  def follow(attribute: String) = {
    WS.url((json \ attribute).as[String]).get
  }

  def embedd(attribute: String): Future[GitHubResource] = {
    Logger.info (s"Trying to embedd attribute $attribute");
    
    GitHub ((json \ attribute).as[String]).map {            
      attributeContent =>
        val transform = (__).json.update((__ \ attribute).json.put(attributeContent.json))
        copy(json = json.transform(transform).get)
    }
  }
}

trait GitHubException extends Exception

case class GitHubForbidden(message : String, rateLimit : Int, rateLimitRest : Long	 ) extends Exception 

object GitHub {
  
  
  def apply (url : String ) : Future [GitHubResource] = {
    WS.url(url).get.flatMap { response =>
      
      val rateInfo = RateInfo (
          response.header("X-RateLimit-Limit").map (_.toLong).getOrElse(-1),
          response.header("X-RateLimit-Remaining").map (_.toLong).getOrElse(-1),
          response.header("X-RateLimit-Reset").map (_.toLong).getOrElse(-1))
      
          
      response.status match {
        case HttpStatus.SC_FORBIDDEN => 
          //Logger.warn("GitHub returned 403")
          Future.failed(new RuntimeException ("Forbidden"))
  
        case HttpStatus.SC_OK =>
          Logger.info ("Remaining Rate Limit: " + rateInfo.remaining)
          Future.successful(GitHubResource (response.json, true, rateInfo))
      }
    }
  }
  
  
  def getUser(name: String) = GitHub (s"https://api.github.com/users/$name")
  
}
