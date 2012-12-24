/*
 * UserAgent.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-12-24
 */ 
package ca.n4dev.redshift.utils;

public enum UserAgent {
	ANDROID("RedShift/1.0"), 
	DESKTOP("RedShift/1.0"), 
	IPHONE("Mozilla/5.0 (iPhone; CPU iPhone OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3"), 
	INTERNETEXPLORER("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)");
	
	String userAgentValue;
	
	UserAgent(String userAgentValue) {
		this.userAgentValue = userAgentValue;
	}
	
	public String getString() {
		return this.userAgentValue;
	}
	
	public static UserAgent from(String value) {
		if (value.equalsIgnoreCase("desktop"))
			return DESKTOP;
		else if (value.equalsIgnoreCase("iphone"))
			return IPHONE;
		else if (value.equalsIgnoreCase("internet explorer"))
			return UserAgent.INTERNETEXPLORER;
		else
			return ANDROID;
	}
}
