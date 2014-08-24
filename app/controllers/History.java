package controllers;

import java.util.List;

import play.libs.Json;
import play.mvc.Result;
import service.HistoryService;

public class History extends Application {

	public static Result index() {
		String uuid =  getSessionId();
		List<String> urls = HistoryService.getSessionHistory(uuid);
		return ok(Json.toJson(urls));
	}
}
