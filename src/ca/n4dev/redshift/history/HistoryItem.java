/*
 * HistoryItem.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-11-25
 */ 
package ca.n4dev.redshift.history;


public class HistoryItem {

	public String url;
	public String title;
	
	public HistoryItem(String title, String url) {
		this.title = title;
		this.url = url;
	}
}
