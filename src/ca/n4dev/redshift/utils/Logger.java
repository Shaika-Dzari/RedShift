/*
 * Logger.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-12-18
 */ 
package ca.n4dev.redshift.utils;

import android.util.Log;

public class Logger {
	
	private static final boolean DEBUG_ENABLE = true;
	
	public static void log(String tag, String msg) {
		if (DEBUG_ENABLE) 
			Log.d(tag, msg);
	}
}
