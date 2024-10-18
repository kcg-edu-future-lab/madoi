package edu.kcg.futurelab.madoi.core.util;

public class StringUtil {
	public static boolean contains(String target, String[] values) {
		for(var v : values) {
			if(v.equals(target)) return true;
		}
		return false;
	}
}
