package controllers

import http.CustomContentType._
import play.api.mvc.Controller
import play.api.libs.json.Json
import play.api.mvc._
import play.Logger

object Users extends Controller {
/*
  
  # USER
  
  class = "user"
  name
  repositories : [{},{},{}]
  orgs
  
  
  # Repository
  
  class = "repository"
  name
  user
  orgs
  
  

  
*/
  
 val json = """ {
    "name": "innoQ",
    "class": "root",
    "children": [
        {
            "name": "phaus",
            "size": 100000,
            "class": "avatar",
            "children": [
                {
                    "name": "repositories",
                    "children": [
                        {
                            "name": "AgglomerativeCluster",
                            "size": 3938
                        },
                        {
                            "name": "CommunityStructure",
                            "size": 3812
                        },
                        {
                            "name": "HierarchicalCluster",
                            "size": 6714
                        },
                        {
                            "name": "MergeEdge",
                            "size": 743
                        }
                    ]
                }
            ]
        },
        {
            "name": "martinei",
            "size": 100000,
            "class": "avatar",
            "children": [
                {
                    "name": "repositories",
                    "children": [
                        {
                            "name": "AgglomerativeCluster",
                            "size": 3938
                        },
                        {
                            "name": "CommunityStructure",
                            "size": 3812
                        },
                        {
                            "name": "HierarchicalCluster",
                            "size": 6714
                        },
                        {
                            "name": "MergeEdge",
                            "size": 743
                        }
                    ]
                }
            ]
        },
        {
            "name": "FND",
            "size": 100000,
            "class": "avatar",
            "children": [
                {
                    "name": "repositories",
                    "children": [
                        {
                            "name": "AgglomerativeCluster",
                            "size": 200000
                        }
                    ]
                }
            ]
        },
        {
            "name": "rstrangh",
            "size": 100000,
            "class": "avatar",
            "children": [
                {
                    "name": "repositories",
                    "children": [
                        {
                            "name": "MergeEdge",
                            "size": 743
                        }
                    ]
                }
            ]
        }
    ]
}"""
 
   
   def user(name : String) = Action {
   Logger.info ("Api return user {}",name);
	 Ok(Json.parse(json))
 }
}