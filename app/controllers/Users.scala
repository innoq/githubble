package controllers

import http.CustomContentType._
import play.api.mvc.Controller
import play.api.libs.json.Json
import play.api.mvc._
import play.Logger
import play.api.libs.ws.WS
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import scala.concurrent.Future
 
object Users extends Controller {

  
 val json = """
 [ {
  "login" : "martinei",
  "name" : "Martin Eigenbrodt",
  "id" : "user_795323",
  "class" : "user",
  "avatar" : "https://avatars.githubusercontent.com/u/795323?v=2&s=64",
  "repositories" : [ {
    "name" : "async-http-client",
    "id" : "repo_6756561"
  } ]
}, {
  "name" : "async-http-client",
  "id" : "repo_6756561"
   "class" : "repo"
} ]
 """   
 
   
   def user(name : String) = Action.async {
        // temporary canned repsonse
       Future.successful(Ok(json))
       
//    GitHub.getUser(name).flatMap { gitHubUser =>
//            
//      val completeGitHubUser = gitHubUser.embedd ("repos_url")
//      
//      completeGitHubUser.map {
//    	  res =>
////    	   Ok (res.json )
//    	  Ok (gitHubUserToOutput (res))
//    	 
//      }
     
   //}
 }
 
 

  def gitHubUserToOutput ( gitHubUser : GitHubResource) : JsValue={
           val jsonTransformer =
             (
             (__ \  'login ).json.copyFrom( (__ \ 'login).json.pick ) and
             (__ \  'name ).json.copyFrom( (__ \ 'name).json.pick ) and
             (__ \  'id).json.copyFrom ( (__ \ 'id).json.pick.map {
               case JsNumber (s) => JsString (s"user_$s")
               case _ @ other  => other 
               
             } ) and
             (__ \ 'class).json.put (JsString("user")) and
             (__ \ 'avatar ).json.copyFrom( (__ \ 'avatar_url).json.pick.map { 
               case JsString (s) => JsString (s+"&s=64")
               case _ @ other  => other 
             }) 
             ) reduce
             
            
            val user = gitHubUser.json.transform(jsonTransformer).get
            // now get all those repositories...            
            
			val reposTransformer= (__ \ 'repos_url).read[JsArray]

           val repos = gitHubUser.json.transform(reposTransformer).get.as[JsArray]
			
           val mappedValue = repos.value.map { jsValue =>
             
             val repoTransformer = 
               (
	               (__ \ 'name).json.copyFrom( (__ \ 'name).json.pick)) and
	               (__ \ 'id).json.copyFrom( (__ \ 'id).json.pick.map {
		               case JsNumber (s) => JsString (s"repo_$s")
		               case _ @ other  => other                                   
	               } 
               ) reduce
               
             jsValue.transform(repoTransformer).get
           }
           
           
           // Put The list of mapped Values als into the original object
           val updateUser = (__).json.update ( (__ \ "repositories").json.put(JsArray (mappedValue)))
           // mappedValue
            // We must fetch the lis of repositories! 
            JsArray (user.transform(updateUser).get +: mappedValue)           
  }
  
   
  
  
}

case class GitHubResource ( json : JsValue) {
  
  def follow (attribute : String)= {
    WS.url( (json \ attribute).as[String]).get
  }
  
  def embedd (attribute : String): Future[GitHubResource] = {
    WS.url( (json \ attribute).as[String]).get.map {
      attributeContent =>
        val transform = (__).json.update ((__ \ attribute).json.put(attributeContent.json ))
        GitHubResource (json.transform(transform).get)            
    }    
  }
}

object GitHub {
  
	def getUser (name : String )={
		WS.url(s"https://api.github.com/users/$name").get.map ( js => GitHubResource (js.json))
	}

}
