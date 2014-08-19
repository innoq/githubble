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
      val result = Users.gitHubUserToOutput(GitHubResource(gitHubJson ) )
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
      

      val links = (result \ "links").as[JsArray]
      (links(0) \ "class").as[String] must equal("owns")
      (links(0) \ "source").as[Int] must equal(0)
      (links(0) \ "target").as[Int] must equal(1)
      (links(0) \ "value").as[Int] must equal(1)
      
// TODO: Check links
      
      /*
{
  "nodes":[
    {"label":"phaus", "class":"user", "id":"u1", "avatar":"https://avatars.githubusercontent.com/u/346361?v=2&s=64"},
    {"label":"phaus/agora", "class":"repo", "id":"r1"},
	{"label":"phaus/aibe", "class":"repo", "id":"r2"},
	{"label":"phaus/annotate", "class":"repo", "id":"r3"},
	{"label":"phaus/AQGridView", "class":"repo", "id":"r4"},
    {"label":"phaus/balancr", "class":"repo", "id":"r5"},
    {"label":"phaus/Bombbear", "class":"repo", "id":"r6"},
	{"label":"phaus/ChatLogConverter", "class":"repo", "id":"r7"},
	{"label":"phaus/contrib", "class":"repo", "id":"r8"},
	{"label":"phaus/dash", "class":"repo", "id":"r9"},
    {"label":"phaus/DiabloMiner", "class":"repo", "id":"r10"},
    {"label":"martinei", "class":"user", "id":"u2", "avatar":"https://avatars.githubusercontent.com/u/795323?v=2&s=64"},
    {"label":"FND", "class":"user", "id":"u3", "avatar":"https://avatars.githubusercontent.com/u/3515?v=2&s=64"},
    {"label":"aheusingfeld", "class":"user", "id":"u4", "avatar":"https://avatars.githubusercontent.com/u/534272?v=2&s=64"},
    {"label":"aheusingfeld.github.io", "class":"repo", "id":"r11"},
    {"label":"aheusingfeld/aim42", "class":"repo", "id":"r12"},    
    {"label":"consolving", "class":"orga", "id":"381849", "avatar":"https://avatars.githubusercontent.com/u/3236400?v=2&s=64"},   
    {"label":"innoQ", "class":"orga", "id":"3236400", "avatar":"https://avatars.githubusercontent.com/u/381849?v=2&s=64"}       
  ],
  "links":[
    {"source":1,"target":0,"value":1, "class":"owns"},
    {"source":2,"target":0,"value":8, "class":"owns"},
    {"source":3,"target":0,"value":8, "class":"owns"},
    {"source":4,"target":0,"value":8, "class":"owns"},
    {"source":5,"target":0,"value":1, "class":"owns"},
    {"source":6,"target":0,"value":1, "class":"owns"},
    {"source":7,"target":0,"value":8, "class":"owns"},
    {"source":8,"target":0,"value":8, "class":"owns"},
    {"source":9,"target":0,"value":8, "class":"owns"},
    {"source":10,"target":0,"value":8, "class":"owns"},

    {"source":11,"target":0,"value":8, "class":"follows"},
    {"source":12,"target":0,"value":8, "class":"follows"},
    {"source":13,"target":0,"value":8, "class":"follows"},
    {"source":14,"target":13,"value":8, "class":"owns"},
    {"source":15,"target":13,"value":8, "class":"owns"},    
    {"source":16,"target":0,"value":8, "class":"member"},  
    {"source":17,"target":0,"value":8, "class":"member"}        
  ]
}       */
    }
  }
  
  

  
}
