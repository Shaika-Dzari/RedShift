/*
 * HomeFactory.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-12-28
 */ 
package ca.n4dev.redshift.controller.web;

import java.util.List;

import ca.n4dev.redshift.bookmark.Bookmark;

public class HomeFactory {
	
	private static final String HOME = "<!DOCTYPE html>" +
										  "<html>" +
										  "<head><title>about:home</title></head>" +
										  "<body>" +
										  "<div id='main'>" +
										  "<img id='imglogo' src='redShift_logo.png' alt='logo' />" +
										  "<br />" +
										  "home..." +
										  "</div>" +
										  "</body>"+
										  "</html>";

	public static String createhomeFromBookmark(List<Bookmark> bookmarks) {
		String home = HOME;
		
		return home;
	}
}
