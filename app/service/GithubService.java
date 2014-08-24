package service;

import java.util.Iterator;

import play.Logger;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GithubService {
	private final static String GITHUB_API_URL = "https://api.github.com";

	private final static String DUMMY_USER = "{\n" + "\"login\": \"phaus\",\n" + "\"id\": 346361,\n"
			+ "\"avatar_url\": \"https://avatars.githubusercontent.com/u/346361?v=2\",\n"
			+ "\"gravatar_id\": \"902a4faaa4de6f6aebd6fd7a9fbab46a\",\n"
			+ "\"url\": \"https://api.github.com/users/phaus\",\n" + "\"html_url\": \"https://github.com/phaus\",\n"
			+ "\"followers_url\": \"https://api.github.com/users/phaus/followers\",\n"
			+ "\"following_url\": \"https://api.github.com/users/phaus/following{/other_user}\",\n"
			+ "\"gists_url\": \"https://api.github.com/users/phaus/gists{/gist_id}\",\n"
			+ "\"starred_url\": \"https://api.github.com/users/phaus/starred{/owner}{/repo}\",\n"
			+ "\"subscriptions_url\": \"https://api.github.com/users/phaus/subscriptions\",\n"
			+ "\"organizations_url\": \"https://api.github.com/users/phaus/orgs\",\n"
			+ "\"repos_url\": \"https://api.github.com/users/phaus/repos\",\n"
			+ "\"events_url\": \"https://api.github.com/users/phaus/events{/privacy}\",\n"
			+ "\"received_events_url\": \"https://api.github.com/users/phaus/received_events\",\n"
			+ "\"type\": \"User\",\n" + "\"site_admin\": false,\n" + "\"name\": \"Philipp Haussleiter\",\n"
			+ "\"company\": \"\",\n" + "\"blog\": \"http://philipp.haussleiter.de\",\n"
			+ "\"location\": \"Germany\",\n" + "\"email\": \"philipp@haussleiter.de\",\n" + "\"hireable\": false,\n"
			+ "\"bio\": null,\n" + "\"public_repos\": 63,\n" + "\"public_gists\": 13,\n" + "\"followers\": 14,\n"
			+ "\"following\": 26,\n" + "\"created_at\": \"2010-07-27T23:58:18Z\",\n"
			+ "\"updated_at\": \"2014-08-22T23:53:02Z\"\n" + "}";
	private final static String X_RATE_LIMIT_HEADER = "X-RateLimit-Limit";
	private final static String X_RATE_REMAIN_HEADER = "X-RateLimit-Remaining";
	private final static String X_RATE_RESET_HEADER = "X-RateLimit-Reset";

	public static Promise<ObjectNode> getUser(String username) {
		String url = GITHUB_API_URL + "/users/" + username;
		Logger.debug("calling: " + url);
		return WS.url(url).get().flatMap(new Function<WSResponse, Promise<ObjectNode>>() {
			@Override
			public Promise<ObjectNode> apply(final WSResponse response) {
				Logger.debug("status: " + response.getStatus() + " content:\n" + response.getBody());
				JsonNode json = response.asJson(); // Json.parse(DUMMY_USER);
													// //response.asJson();
				final ObjectNode newResult = mapRateLimitStatus(response, Json.newObject());
				final ObjectNode result = mapUser(json, newResult);
				final Promise<WSResponse> followerCall = WS.url(getEntry(json, "followers_url")).get();
				final Promise<WSResponse> reposCall = WS.url(getEntry(json, "repos_url")).get();
				final Promise<WSResponse> orgasCall = WS.url(getEntry(json, "organizations_url")).get();
				return followerCall.flatMap(new Function<WSResponse, Promise<ObjectNode>>() {
					@Override
					public Promise<ObjectNode> apply(final WSResponse followerResult) {
						return reposCall.flatMap(new Function<WSResponse, Promise<ObjectNode>>() {
							@Override
							public Promise<ObjectNode> apply(final WSResponse reposResult) {
								return orgasCall.map(new Function<WSResponse, ObjectNode>() {
									public ObjectNode apply(final WSResponse orgasResult) {
										return combineResults(result, followerResult, reposResult, orgasResult);
									}
								});
							}
						});
					}
				});
			}
		});
	}

	private static ObjectNode mapRateLimitStatus(WSResponse response, final ObjectNode result) {
		ObjectNode status = result.objectNode();
		status.put("remaining", response.getHeader(X_RATE_REMAIN_HEADER));
		status.put("reset", response.getHeader(X_RATE_RESET_HEADER));
		status.put("limit", response.getHeader(X_RATE_LIMIT_HEADER));
		result.put("status", status);
		return result;
	}

	private static ObjectNode mapUser(JsonNode json, final ObjectNode result) {
		Logger.debug("json: " + Json.stringify(json));
		ArrayNode nodes = (ArrayNode) (result.get("nodes") != null ? result.path("nodes") : result.arrayNode());
		ArrayNode links = (ArrayNode) (result.get("links") != null ? result.path("links") : result.arrayNode());
		ObjectNode root = result.objectNode();
		if (getEntry(json, "type").equals("User")) {
			root.put("label", getEntry(json, "login"));
			root.put("id", "u" + getEntry(json, "id"));
			root.put("class", "user");
			root.put("name", getEntry(json, "name"));
			root.put("avatar", getEntry(json, "avatar_url") + "&s=64");
		}
		nodes.add(root);
		result.put("nodes", nodes);
		result.put("links", links);
		return result;
	}

	private static ObjectNode combineResults(ObjectNode result, WSResponse followerResult, WSResponse reposResult,
			WSResponse orgasResult) {
		JsonNode element;
		if (followerResult != null) {
			JsonNode json = followerResult.asJson();
			Iterator<JsonNode> elements = json.elements();
			while (elements.hasNext()) {
				elements.next();
			}
		}
		return result;
	}

	private static String getEntry(JsonNode node, String key) {
		JsonNode child = node.path(key);
		Logger.debug("key: " + key + " got: " + Json.stringify(child));
		if (child != null) {
			return child.isTextual() ? child.textValue() : String.valueOf(child.intValue());
		}
		return null;
	}

}
