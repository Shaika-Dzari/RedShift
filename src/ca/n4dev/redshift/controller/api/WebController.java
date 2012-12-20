/*
 * WebController.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-12-04
 */ 
package ca.n4dev.redshift.controller.api;

import android.os.Bundle;
import android.util.SparseArray;

public interface WebController {
	
	public static final String HOME = "file:///android_asset/home.html";
	
	public boolean goBack();
	public boolean goForward();
	public void goTo(String url);
	public void refresh();
	public String currentUrl();
	public int newTab();
	public SparseArray<String> listTab();
	public void closeTab(int tabId);
	public void setCurrentTab(int tabId);
	public void saveState(Bundle outstate);
	public void restoreState(Bundle outstate);
	
}
