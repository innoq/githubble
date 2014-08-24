package controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import play.libs.F.Function;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;
import service.GithubService;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class Repos extends Controller {
	// TODO handle exception :-)
	public static Promise<Result> show(String reponame) throws UnsupportedEncodingException {
		return GithubService.getRepo(URLDecoder.decode(reponame, "UTF-8")).map(new Function<ObjectNode, Result>() {
			@Override
			public Result apply(ObjectNode resultNode) {
				return ok(resultNode);
			}
		});
	}
}
