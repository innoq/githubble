package controllers

import play.api.mvc.Controller
import play.Logger
import play.api.libs.ws.WS
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.libs.functional._


import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import service.GitHub
import service.GitHubResource
 
object Users extends Controller {

     
   def user(name : String) = Action.async {
    for {
    	 gitHubUser <- GitHub.getUser(name)    	 
    	 withRepos <-  gitHubUser.embedd ("repos_url")
    	 x <- {System.out.println (withRepos.json); Future{gitHubUser} }
    	 withOrgs <- withRepos.embedd("organizations_url")
    	 withFollower <- withOrgs.embedd("followers_url") 
      } yield {
        
    	if (withFollower.success ) {
    		Ok (gitHubUserToOutput (withFollower)) 
    	} else {
    	    TooManyRequest  (Json.toJson (withFollower.rateInfo) )    	  
    	}
      }
 	}
 
 
  def copyField (name : String) : Reads[JsObject] = {
     (__ \  name ).json.copyFrom(Reads.nullable[JsString]( (__ \ name)).map {       
     	case Some(s) => s      	
        case _ => JsString("")
     })
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

  implicit val JsArrayReducer = Reducer[JsArray, JsArray](js => js)
  
  def gitHubUserToOutput ( gitHubUser : GitHubResource) : JsValue={

    
    def arrayRead[T <: JsValue](singleRead : Reads[T]) : Reads[JsArray]= {
      val arrayRead = implicitly [Reads[JsArray]]
      arrayRead.map({ array =>
      JsArray(array.value.map { item =>
        item.transform(singleRead).get
      })
    })
    }
    
    // a transformer to create a single user object from gitHubUser json
    val userTransformer =
      (
        copyField("label", "login") and
        copyField("name") and
        mapId("user") and
        put("class", "user") and
        (__ \ 'avatar).json.copyFrom((__ \ 'avatar_url).json.pick.map {
          case JsString(s) => JsString(s + "&s=64")
          case _@ other => other
        })) reduce

    // transform a single gitHub Repository entry into a node entry
    val singleRepoTransformer =
      (
        copyField("name") and
        copyField("label", "full_name") and
        put("class", "repo") and
        mapId("repo")) reduce

    // Transform a list of repos
    //Todo create an generic JsArray Mapping transformer
    val reposTransformer = (__ \ 'repos_url).read(arrayRead(singleRepoTransformer))

    val singleOrgTransformer = (
      copyField("label", "login") and
      put("class", "orga") and
      mapId("orga") and
      (__ \ 'avatar).json.copyFrom((__ \ 'avatar_url).json.pick.map {
          case JsString(s) => JsString(s + "&s=64")
          case _@ other => other
        })
    ) reduce
    
    val orgsTransformer = (__ \ 'organizations_url).read(arrayRead(singleOrgTransformer))

    val followersTransformer = (__ \ 'followers_url).read(arrayRead(userTransformer))
    val userAsSingleSeq = userTransformer.map { js => JsArray(Seq(js)) }

    val nodesWithoutUserAsArrayTransformer =  (reposTransformer and orgsTransformer and followersTransformer).reduce 
    val nodes = (__ \ "nodes").json.copyFrom((userAsSingleSeq and nodesWithoutUserAsArrayTransformer) reduce)
    
    
    
    
    def generateLinksTransformer (sourceList : Reads[JsArray], relType : String)= {
      sourceList.map {
    	   node =>
      JsArray(node.value.map {
        repo =>
          repo.transform(
            (put("class", relType) and
             put("value", 1) and
           put("source", 0) 
          ) reduce).get
      })                  
    }
    }
 
       //put("target", idx + 1)
    // A link from the user to each repo
    val linksArrayTransformer =  ((
    	generateLinksTransformer (reposTransformer, "owns") and
    	generateLinksTransformer (orgsTransformer, "member") and
    	generateLinksTransformer (followersTransformer, "follows")) reduce)
    
   val linksArrayTransformerWithTarget = linksArrayTransformer.map { ar =>
     JsArray(ar.value.zipWithIndex.map {case (it, idx) =>        
       (it.transform (
           (__).json.update(put ("target",idx+1))    		   
       ).get)
     }      )
    }     	
    
    
    val links = (__ \ "links").json.copyFrom(linksArrayTransformerWithTarget)
    // a Transformer to create the overall result  from gitHubUser
    
    val rateInfo = ( __ \ "status").json.put (Json.toJson (gitHubUser.rateInfo ))
    val resultTransformer = (nodes and links and rateInfo) reduce

    gitHubUser.json.get.transform(resultTransformer).get

  }
                  
}


