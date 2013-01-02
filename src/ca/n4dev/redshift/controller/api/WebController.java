/*
 * WebController.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-12-26
 */ 
package ca.n4dev.redshift.controller.api;

import android.os.Message;

public interface WebController extends TabController, NavigationController {
	public void clearCache();
	public void clearformData();
	public void handleMessage(Message msg);
}
