package controllers;

import play.libs.F.Function;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;
import service.GithubService;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class Users extends Controller {
	
	public static Promise<Result> show(String username){
		return GithubService.getUser(username).map(new Function<ObjectNode, Result>(){
			@Override
			public Result apply(ObjectNode resultNode) {
				return ok(resultNode);
			}
		});
	}
}
