package controllers;

import play.libs.F.Function;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;
import service.GithubService;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class Orgas extends Controller {
	
	public static Promise<Result> show(String organame){
		return GithubService.getOrga(organame).map(new Function<ObjectNode, Result>(){
			@Override
			public Result apply(ObjectNode resultNode) {
				// TODO int => String => int  not very nice :-/
				String http = GithubService.getEntry(resultNode, "http");
				if(http.equals("200")) {
					return ok(resultNode);					
				} else {
					return status(Integer.parseInt(http), resultNode);
				}
			}
		});
	}
}
