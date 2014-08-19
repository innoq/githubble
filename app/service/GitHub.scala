package service

import play.api.libs.ws.WS
import play.api.Play.current
import play.api.libs.functional.syntax._
import play.api.libs.json._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger

case class GitHubResource(json: JsValue) {

  def follow(attribute: String) = {
    WS.url((json \ attribute).as[String]).get
  }

  def embedd(attribute: String): Future[GitHubResource] = {
    Logger.info (s"Trying to embedd attribute $attribute");
    
    GitHub ((json \ attribute).as[String]).map {            
      attributeContent =>
        val transform = (__).json.update((__ \ attribute).json.put(attributeContent.json))
        GitHubResource(json.transform(transform).get)
    }
  }
}

object GitHub {
  
  def apply (url : String) = {
    Logger.info (s"Calling GitHub: $url")
     WS.url (url).get.map { result =>
       Logger.debug ("Result: " + result.status);
       GitHubResource (result.json)
     }
  }
  
  def getUser(name: String) = {
    WS.url(s"https://api.github.com/users/$name").get.map(js => GitHubResource(js.json))
  }
}
