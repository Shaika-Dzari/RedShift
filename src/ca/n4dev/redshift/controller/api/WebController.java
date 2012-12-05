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

public interface WebController {
	public void goBack();
	public void goForward();
	public void goTo(String url);
	public void refresh();
	public int newTab();
	public void closeTab(int tabId);
	public void setCurrentTab(int tabId);
}
