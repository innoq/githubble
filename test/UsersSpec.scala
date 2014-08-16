import org.scalatest._
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import controllers.Users
import play.api.libs.json.Json
import play.api.libs.json.JsArray
import controllers.GitHubResource
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._



@RunWith(classOf[JUnitRunner])
class UsersSpec extends PlaySpec  {

  
  val gitHubJson = Json.parse( """
{
    "login": "martinei",
    "id": 795323,
    "avatar_url": "https://avatars.githubusercontent.com/u/795323?v=2",
    "gravatar_id": "27a1e823747965ccbdc0039b31bc58ba",
    "url": "https://api.github.com/users/martinei",
    "html_url": "https://github.com/martinei",
    "followers_url": "https://api.github.com/users/martinei/followers",
    "following_url": "https://api.github.com/users/martinei/following{/other_user}",
    "gists_url": "https://api.github.com/users/martinei/gists{/gist_id}",
    "starred_url": "https://api.github.com/users/martinei/starred{/owner}{/repo}",
    "subscriptions_url": "https://api.github.com/users/martinei/subscriptions",
    "organizations_url": "https://api.github.com/users/martinei/orgs",
    "repos_url": [        
            {
                "id": 6756561,
                "name": "async-http-client",
                "full_name": "martinei/async-http-client",
                "owner": {
                    "login": "martinei",
                    "id": 795323,
                    "avatar_url": "https://avatars.githubusercontent.com/u/795323?v=2",
                    "gravatar_id": "27a1e823747965ccbdc0039b31bc58ba",
                    "url": "https://api.github.com/users/martinei",
                    "html_url": "https://github.com/martinei",
                    "followers_url": "https://api.github.com/users/martinei/followers",
                    "following_url": "https://api.github.com/users/martinei/following{/other_user}",
                    "gists_url": "https://api.github.com/users/martinei/gists{/gist_id}",
                    "starred_url": "https://api.github.com/users/martinei/starred{/owner}{/repo}",
                    "subscriptions_url": "https://api.github.com/users/martinei/subscriptions",
                    "organizations_url": "https://api.github.com/users/martinei/orgs",
                    "repos_url": "https://api.github.com/users/martinei/repos",
                    "events_url": "https://api.github.com/users/martinei/events{/privacy}",
                    "received_events_url": "https://api.github.com/users/martinei/received_events",
                    "type": "User",
                    "site_admin": false
                },
                "private": false,
                "html_url": "https://github.com/martinei/async-http-client",
                "description": "Asynchronous Http Client for Java",
                "fork": true,
                "url": "https://api.github.com/repos/martinei/async-http-client",
                "forks_url": "https://api.github.com/repos/martinei/async-http-client/forks",
                "keys_url": "https://api.github.com/repos/martinei/async-http-client/keys{/key_id}",
                "collaborators_url": "https://api.github.com/repos/martinei/async-http-client/collaborators{/collaborator}",
                "teams_url": "https://api.github.com/repos/martinei/async-http-client/teams",
                "hooks_url": "https://api.github.com/repos/martinei/async-http-client/hooks",
                "issue_events_url": "https://api.github.com/repos/martinei/async-http-client/issues/events{/number}",
                "events_url": "https://api.github.com/repos/martinei/async-http-client/events",
                "assignees_url": "https://api.github.com/repos/martinei/async-http-client/assignees{/user}",
                "branches_url": "https://api.github.com/repos/martinei/async-http-client/branches{/branch}",
                "tags_url": "https://api.github.com/repos/martinei/async-http-client/tags",
                "blobs_url": "https://api.github.com/repos/martinei/async-http-client/git/blobs{/sha}",
                "git_tags_url": "https://api.github.com/repos/martinei/async-http-client/git/tags{/sha}",
                "git_refs_url": "https://api.github.com/repos/martinei/async-http-client/git/refs{/sha}",
                "trees_url": "https://api.github.com/repos/martinei/async-http-client/git/trees{/sha}",
                "statuses_url": "https://api.github.com/repos/martinei/async-http-client/statuses/{sha}",
                "languages_url": "https://api.github.com/repos/martinei/async-http-client/languages",
                "stargazers_url": "https://api.github.com/repos/martinei/async-http-client/stargazers",
                "contributors_url": "https://api.github.com/repos/martinei/async-http-client/contributors",
                "subscribers_url": "https://api.github.com/repos/martinei/async-http-client/subscribers",
                "subscription_url": "https://api.github.com/repos/martinei/async-http-client/subscription",
                "commits_url": "https://api.github.com/repos/martinei/async-http-client/commits{/sha}",
                "git_commits_url": "https://api.github.com/repos/martinei/async-http-client/git/commits{/sha}",
                "comments_url": "https://api.github.com/repos/martinei/async-http-client/comments{/number}",
                "issue_comment_url": "https://api.github.com/repos/martinei/async-http-client/issues/comments/{number}",
                "contents_url": "https://api.github.com/repos/martinei/async-http-client/contents/{+path}",
                "compare_url": "https://api.github.com/repos/martinei/async-http-client/compare/{base}...{head}",
                "merges_url": "https://api.github.com/repos/martinei/async-http-client/merges",
                "archive_url": "https://api.github.com/repos/martinei/async-http-client/{archive_format}{/ref}",
                "downloads_url": "https://api.github.com/repos/martinei/async-http-client/downloads",
                "issues_url": "https://api.github.com/repos/martinei/async-http-client/issues{/number}",
                "pulls_url": "https://api.github.com/repos/martinei/async-http-client/pulls{/number}",
                "milestones_url": "https://api.github.com/repos/martinei/async-http-client/milestones{/number}",
                "notifications_url": "https://api.github.com/repos/martinei/async-http-client/notifications{?since,all,participating}",
                "labels_url": "https://api.github.com/repos/martinei/async-http-client/labels{/name}",
                "releases_url": "https://api.github.com/repos/martinei/async-http-client/releases{/id}",
                "created_at": "2012-11-19T07:36:32Z",
                "updated_at": "2013-01-17T05:53:21Z",
                "pushed_at": "2012-11-19T07:43:42Z",
                "git_url": "git://github.com/martinei/async-http-client.git",
                "ssh_url": "git@github.com:martinei/async-http-client.git",
                "clone_url": "https://github.com/martinei/async-http-client.git",
                "svn_url": "https://github.com/martinei/async-http-client",
                "homepage": "",
                "size": 2019,
                "stargazers_count": 0,
                "watchers_count": 0,
                "language": "Java",
                "has_issues": false,
                "has_downloads": true,
                "has_wiki": true,
                "forks_count": 0,
                "mirror_url": null,
                "open_issues_count": 0,
                "forks": 0,
                "open_issues": 0,
                "watchers": 0,
                "default_branch": "master"
            }
        ],
    "events_url": "https://api.github.com/users/martinei/events{/privacy}",
    "received_events_url": "https://api.github.com/users/martinei/received_events",
    "type": "User",
    "site_admin": false,
    "name": "Martin Eigenbrodt",
    "company": "innoQ",
    "blog": "",
    "location": "",
    "email": "",
    "hireable": false,
    "bio": null,
    "public_repos": 12,
    "public_gists": 1,
    "followers": 2,
    "following": 0,
    "created_at": "2011-05-18T08:33:20Z",
    "updated_at": "2014-08-16T13:12:01Z"
}
    """)
    
  "Users" should {

    "map github User correctly" in {
      val result = Users.gitHubUserToOutput(GitHubResource(gitHubJson ) )
      System.out.println("Result is: " + Json.prettyPrint(result))
      // mus be an array
      val array = result.as[JsArray]
      //[{"login":"martinei","name":"Martin Eigenbrodt"}]
      
      (result(0)\"login").as[String]  must equal ("martinei")
      (result(0)\"name").as[String]  must equal ("Martin Eigenbrodt")
      (result(0)\"class").as[String] must equal ("user")
      (result(0)\"id").as[String] must equal ("user_795323")
      
    }
  }
  
  
  "understand Array read" should {
  
    //  picking
    
    val jsonTransformer = (__ \ 'key2 \ 'key23).json.pick
    //pick typed  (__ \ 'key2 \ 'key23).json.pick[JsArray] 
    // copyFrom
    // update
    // put
    // prune
    
    
    val arr = Json.parse ("""
        [1,2,3,4,5,6,7]        
        """)
        
    
        
    
  }
  
 /*[
   {
       "name": "martinei",
       "class": "user",
       "id": "user_23"
   },
   {
       "name": "phl",
       "class": "user",
       "id": "user_42",
       "repositories": [
           {
               "id": "repo_3"
           }
       ]
   },
   {
       "name": "phl/githubble",
       "class": "repository",
       "id": "repo_3"
   }
]*/ 
  
}
