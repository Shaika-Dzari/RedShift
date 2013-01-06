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

import android.content.Context;

import ca.n4dev.redshift.bookmark.Bookmark;
import ca.n4dev.redshift.persistence.BookmarkDbHelper;

public class HomeFactory {
	
	private String homePage = null;
	private Context context = null;
	
	private static final String HOME = "<!DOCTYPE html>" +
										  "<html>" +
										  "<head>" +
										  "<title>redshift:home</title>" +
										  "<meta name='viewport' content='width=800' />" +
										  "<link type='text/css' rel='stylesheet' href='home.css' />" +
										  "</head>" +
										  "<body>" +
										  "<div id='main'>" +
										  "<img id='imglogo' src='redShift_logo.png' alt='logo' />" +
										  "<br />" +
										  "__BOOKMARK__" +
										  "</div>" +
										  "</body>"+
										  "</html>";

	public HomeFactory(Context context) {
		this.context = context;
	}
	
	public String getHomePage(boolean refresh) {
		
		if (homePage == null || refresh) {
			StringBuilder str = new StringBuilder();
			BookmarkDbHelper helper = new BookmarkDbHelper(context);
			helper.openDb();
			List<Bookmark> lb = helper.getHomeBookmark();
			helper.closeDb();
			
			for (Bookmark b : lb) {
				str.append("<a href='" + b.url + "' class='dial'>");
				str.append("<span class='title'>" + b.title + "</span><br />");
				str.append("<span class='url'>" + b.url + "</span>");
				str.append("</a>");
				
			}
			
			homePage = HOME.replace("__BOOKMARK__", str.toString());
		}
		
		return homePage;
	}
}
