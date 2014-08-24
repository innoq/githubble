package service;

import java.util.ArrayList;
import java.util.List;

import play.Logger;
import play.cache.Cache;

public class HistoryService {
	@SuppressWarnings("unchecked")
	public static List<String> getSessionHistory(String uuid) {
		List<String> entries = new ArrayList<String>();
		Object o = Cache.get(uuid);
		if (o != null && o instanceof List) {
			entries = (List<String>) o;
		}
		return entries;
	}

	@SuppressWarnings("unchecked")
	public static void addSessionHistoryEntry(String uuid, String entry) {
		List<String> entries = new ArrayList<String>();
		Object o = Cache.get(uuid);
		if (o != null && o instanceof List) {
			entries = (List<String>) o;
		}
		if(!entries.contains(entry)){
			Logger.debug("adding " + entry + " for " + uuid + " to Cache");
			entries.add(entry);
			Cache.set(uuid, entries);			
		}
	}
}
