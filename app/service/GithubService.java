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
				final ObjectNode result = mapNode(json, newResult);
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

	private static ObjectNode mapNode(JsonNode json, final ObjectNode result) {
		Logger.debug("json: " + Json.stringify(json));
		ArrayNode nodes = (ArrayNode) (result.get("nodes") != null ? result.path("nodes") : result.arrayNode());
		ObjectNode root = result.objectNode();
		if (getEntry(json, "type").equals("User")) {
			root.put("label", getEntry(json, "login"));
			root.put("id", "u" + getEntry(json, "id"));
			root.put("class", "user");
			root.put("name", getEntry(json, "name"));
			root.put("avatar", getEntry(json, "avatar_url") + "&s=64");
		}

		if (getEntry(json, "git_url").startsWith("git")) {
			root.put("label", getEntry(json, "full_name"));
			root.put("id", "r" + getEntry(json, "id"));
			root.put("class", "repo");
			root.put("name", getEntry(json, "name"));
		}

		if (getEntry(json, "public_members_url").startsWith("https")) {
			root.put("label", getEntry(json, "login"));
			root.put("id", "o" + getEntry(json, "id"));
			root.put("class", "orga");
			root.put("name", getEntry(json, "name"));
			root.put("avatar", getEntry(json, "avatar_url") + "&s=64");
		}
		nodes.add(root);
		result.put("nodes", nodes);

		return result;
	}

	private static ObjectNode mapLink(JsonNode json, final ObjectNode result, String type) {
		ArrayNode links = (ArrayNode) (result.get("links") != null ? result.path("links") : result.arrayNode());
		ArrayNode nodes = (ArrayNode) (result.get("nodes") != null ? result.path("nodes") : result.arrayNode());
		ObjectNode link = result.objectNode();
		link.put("value", 1);
		link.put("class", type);
		link.put("source", 0);
		link.put("target", nodes.size() - 1);
		links.add(link);
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
				element = elements.next();
				result = mapNode(element, result);
				result = mapLink(json, result, "follows");
			}
		}
		if (reposResult != null) {
			JsonNode json = reposResult.asJson();
			Iterator<JsonNode> elements = json.elements();
			while (elements.hasNext()) {
				element = elements.next();
				result = mapNode(element, result);
				result = mapLink(json, result, "owns");
			}
		}

		if (orgasResult != null) {
			JsonNode json = orgasResult.asJson();
			Iterator<JsonNode> elements = json.elements();
			while (elements.hasNext()) {
				element = elements.next();
				result = mapNode(element, result);
				result = mapLink(json, result, "member");
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
		return "";
	}

}
