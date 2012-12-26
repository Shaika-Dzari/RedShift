/*
 * UrlUtils.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-11-25
 */ 
package ca.n4dev.redshift.utils;

public class UrlUtils {

	public static String sanitize(String url) {
		if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("file://")) {
			return "http://" + url;
		}
		return url;
	}
	
	public static boolean externalHandling(String url) {
		
		return false;
	}
}
