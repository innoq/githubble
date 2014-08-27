package controllers;

import play.libs.F.Function;
import play.libs.F.Promise;
import play.mvc.Result;
import service.GithubService;
import service.HistoryService;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class Users  extends Application {
	
	public static Promise<Result> show(String username){
		String uuid =  getSessionId();
		HistoryService.addSessionHistoryEntry(uuid, "user/"+username);
		return GithubService.getUser(username).map(new Function<ObjectNode, Result>(){
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
