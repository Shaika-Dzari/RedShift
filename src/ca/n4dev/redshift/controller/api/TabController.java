/*
 * TabController.java
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

import java.util.List;

import ca.n4dev.redshift.controller.container.RsWebView;

import android.app.Activity;

public interface TabController {
	public int newTab(Activity activity, boolean privateBrowsing) throws TooManyTabException;
	public void closeTab(int tabId);
	public void setCurrentTab(int tabId);
	public List<RsWebView> listTab();
}
