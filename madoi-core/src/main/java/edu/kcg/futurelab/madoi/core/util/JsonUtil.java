package edu.kcg.futurelab.madoi.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.LRUMap;

public class JsonUtil {
	public static synchronized String toString(Object object) {
		String ret = JsonUtil.cache.get(object);
		if(ret == null) {
			try {
				ret = om.writeValueAsString(object);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}
		cache.put(object, ret);
		return ret;
	}
	
	private static LRUMap<Object, String> cache = new LRUMap<>(100, 200);
	private static ObjectMapper om = new ObjectMapper();
}
