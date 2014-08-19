import org.scalatest._
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import controllers.Users
import play.api.libs.json.Json
import play.api.libs.json.JsArray
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import service.GitHubResource
import service.RateInfo



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
    "followers_url": [ {
	    "login": "simono",
	    "id": 874148,
	    "avatar_url": "https://avatars.githubusercontent.com/u/874148?v=2",
	    "gravatar_id": "8fddc92f5a3e278496e2fce9293f25ac",
	    "url": "https://api.github.com/users/simono",
	    "html_url": "https://github.com/simono",
	    "followers_url": "https://api.github.com/users/simono/followers",
	    "following_url": "https://api.github.com/users/simono/following{/other_user}",
	    "gists_url": "https://api.github.com/users/simono/gists{/gist_id}",
	    "starred_url": "https://api.github.com/users/simono/starred{/owner}{/repo}",
	    "subscriptions_url": "https://api.github.com/users/simono/subscriptions",
	    "organizations_url": "https://api.github.com/users/simono/orgs",
	    "repos_url": "https://api.github.com/users/simono/repos",
	    "events_url": "https://api.github.com/users/simono/events{/privacy}",
	    "received_events_url": "https://api.github.com/users/simono/received_events",
	    "type": "User",
	    "site_admin": false
		  }],
    "following_url": "https://api.github.com/users/martinei/following{/other_user}",
    "gists_url": "https://api.github.com/users/martinei/gists{/gist_id}",
    "starred_url": "https://api.github.com/users/martinei/starred{/owner}{/repo}",
    "subscriptions_url": "https://api.github.com/users/martinei/subscriptions",
    "organizations_url": 
		[
		  {
		    "login": "jenkinsci",
		    "id": 107424,
		    "url": "https://api.github.com/orgs/jenkinsci",
		    "repos_url": "https://api.github.com/orgs/jenkinsci/repos",
		    "events_url": "https://api.github.com/orgs/jenkinsci/events",
		    "members_url": "https://api.github.com/orgs/jenkinsci/members{/member}",
		    "public_members_url": "https://api.github.com/orgs/jenkinsci/public_members{/member}",
		    "avatar_url": "https://avatars.githubusercontent.com/u/107424?v=2"
		  }
		] ,      
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
      val result = Users.gitHubUserToOutput(GitHubResource(Some(gitHubJson),true,RateInfo(0,0,0) ) )
      System.out.println("Result is: " + Json.prettyPrint(result))
      // mus be an array

      //[{"login":"martinei","name":"Martin Eigenbrodt"}]
      // All nodes of the grapd
      val nodes = (result \ "nodes").as[JsArray]
      //(nodes(0) \ "login").as[String]  must equal ("martinei")
      (nodes(0) \ "label").as[String]  must equal ("martinei")
      (nodes(0) \ "class").as[String] must equal ("user")
      (nodes(0) \ "id").as[String] must equal ("user_795323")
      (nodes(0) \ "avatar").as[String] must equal ("https://avatars.githubusercontent.com/u/795323?v=2&s=64")

      // Second Node is expected to be the repository 
      (nodes(1) \ "id").as[String]  must equal ("repo_6756561")
      (nodes(1) \ "label").as[String]  must equal ("martinei/async-http-client")
      (nodes(1) \ "class").as[String] must equal ("repo")
      
      
      // Third node should be the organisation
      (nodes(2) \ "id").as[String]  must equal ("orga_107424")
      (nodes(2) \ "label").as[String]  must equal ("jenkinsci")
      (nodes(2) \ "class").as[String] must equal ("orga")
      (nodes(2) \ "avatar").as[String] must equal ("https://avatars.githubusercontent.com/u/107424?v=2&s=64")     

      // No my fellow follower      
      (nodes(3) \ "id").as[String]  must equal ("user_874148")
      (nodes(3) \ "label").as[String]  must equal ("simono")
      (nodes(3) \ "class").as[String] must equal ("user")
      (nodes(3) \ "avatar").as[String] must equal ("https://avatars.githubusercontent.com/u/874148?v=2&s=64")

      
      val links = (result \ "links").as[JsArray]
      (links(0) \ "class").as[String] must equal("owns")
      (links(0) \ "source").as[Int] must equal(0)
      (links(0) \ "target").as[Int] must equal(1)
      (links(0) \ "value").as[Int] must equal(1)

      (links(1) \ "class").as[String] must equal("member")
      (links(1) \ "source").as[Int] must equal(0)
      (links(1) \ "target").as[Int] must equal(2)
      (links(1) \ "value").as[Int] must equal(1)
      
      (links(2) \ "class").as[String] must equal("follows")
      (links(2) \ "source").as[Int] must equal(0)
      (links(2) \ "target").as[Int] must equal(3)
      (links(2) \ "value").as[Int] must equal(1)      

    }
  }
  
  

  
}
