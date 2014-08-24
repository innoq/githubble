package controllers;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import tyrex.services.UUID;
import views.html.index;

public class Application extends Controller {

	public static Result index() {
		return ok(index.render());
	}

	protected static String getSessionId() {
		String uuid = session("uuid");
		if (uuid == null) {
			uuid = UUID.create();
			Logger.debug("creating session " + uuid);
			session("uuid", uuid);
		}
		return uuid;
	}
}
