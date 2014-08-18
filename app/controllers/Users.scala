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
import Reads.constraints._
import play.api.libs.json.util._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import scala.concurrent.Future
import play.api.libs.functional.Monoid
import play.api.libs.functional.FunctionalBuilder
import play.api.libs.functional.Reducer
 
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
  "id" : "repo_6756561",
   "class" : "repo"
} ]
 """   
 
   
   def user(name : String) = Action.async {
        // temporary canned repsonse
//       Future.successful(Ok(json))
       
    GitHub.getUser(name).flatMap { gitHubUser =>
            
      val completeGitHubUser = gitHubUser.embedd ("repos_url")
      
      completeGitHubUser.map {
    	  res =>
//    	   Ok (res.json )
    	  Ok (gitHubUserToOutput (res))
    	 
      }
     
   }
 }
 
 
  def copyField (name : String) : Reads[JsObject] = {
     (__ \  name ).json.copyFrom( (__ \ name).json.pick ) 
  }

  def copyField (to : String, from : String) : Reads[JsObject] = {
     (__ \  to ).json.copyFrom( (__ \ from).json.pick ) 
  }
  
  def mapId(prefix: String): Reads[JsObject] = {
    (__ \ 'id).json.copyFrom((__ \ 'id).json.pick.map {
      case JsNumber(s) => JsString(s"${prefix}_$s")
      case _@ other => other

    })
  }
  
  def put (field : String, value : String) : Reads[JsObject] = {
		  (__ \  field ).json.put (JsString(value))    
  }

  def put (field : String, value : Int) : Reads[JsObject] = {
		  (__ \  field ).json.put (JsNumber(value))    
  }

  
  
   
  case class User(login : String, name :String,  id : Int, avatar : String)
  
  def gitHubUserToOutput ( gitHubUser : GitHubResource) : JsValue={
		  
		  // user
		  // repos
		  // concatenation liefert denen indexe
    
		  // link 
    
//	  	  val users : Reads[User] =  	    (
//	  	    	(__ \ 'login).read[String] and
//	  	    	(__ \ 'name).read[String] and
//	  	    	(__ \ 'id).read[Int] and
//	  	  	  	(__ \ 'avatar_url).read[String]	
//	  	    ) (User)
//    
	  	   
	  	    //gitHubUser.read[user]
		  // a transformer to create a single user object from gitHubUser json
           val userTransformer =
             (
               copyField("label", "login") and
               copyField ("name") and
               mapId("user") and
               put ("class", "user") and               
               (__ \ 'avatar ).json.copyFrom( (__ \ 'avatar_url).json.pick.map { 
               	case JsString (s) => JsString (s+"&s=64")
               	case _ @ other  => other 
               }) 
             ) reduce
             
             
             // transform a single gitHub Repository entry into a node entry
             val singleRepoTransformer =
               (
	               copyField ("name") and
	               copyField ("label", "full_name") and
	               put ("class", "repo") and
	               mapId ("repo")
               ) reduce               
             
             // Transform a list of repos
               //Todo create an generic JsArray Mapping transformer
             val reposTransformer= (__ \ 'repos_url).read[JsArray].map( { repos =>               
               JsArray (repos.value.map { repo =>
                repo.transform (singleRepoTransformer).get 
               })                            	
             } )
             
             val userAsSingleSeq = userTransformer.map { js => JsArray(Seq(js))}
           
           
      implicit val JsArrayReducer = Reducer[JsArray, JsArray](js => js)
      val nodes =  (__ \ "nodes").json.copyFrom (
          (userAsSingleSeq and reposTransformer).reduce
          )
             
             val singleRepoToLinkTransformer =
               (

	               put ("class", "owns") and
	               put ("value", 1) and
	               put ("source", 0) 
               ) reduce               
          
          // A link from the user to each repo
          val linksArrayTransformer= (__ \ 'repos_url).read[JsArray].map( { repos =>
            
            
            	
               JsArray (repos.value.zipWithIndex.map { case (repo, idx) =>
                repo.transform (
                    (singleRepoToLinkTransformer and put ("target", idx+1) ) reduce                    
                ).get 
               })                            	
             } )          
          val links = (__ \ "links").json.copyFrom (linksArrayTransformer          
          )
	     // a Transformer to create the overall result  from gitHubUser
	     val resultTransformer = (nodes and links) reduce
               
              gitHubUser.json.transform(resultTransformer).get 
             
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
