/*
 * WebViewOnMenuItemClickListener.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-12-26
 */ 
package ca.n4dev.redshift.events;

import ca.n4dev.redshift.controller.api.TooManyTabException;
import ca.n4dev.redshift.controller.api.WebController;
import android.app.Activity;
import android.view.MenuItem;
import android.widget.Toast;

public class WebViewOnMenuItemClickListener implements MenuItem.OnMenuItemClickListener {

	private WebController webController;
	private String url;
	private boolean privateBrowsing;
	private Activity parent;
	
	public WebViewOnMenuItemClickListener(Activity parent, WebController webController, String url) {
		this(parent, webController, url, false);
	}
	
	public WebViewOnMenuItemClickListener(Activity parent, WebController webController, String url, boolean privateBrowsing) {
		this.webController = webController;
		this.url = url;
		this.privateBrowsing = privateBrowsing;
		this.parent = parent;
	}
	
	/* (non-Javadoc)
	 * @see android.view.MenuItem.OnMenuItemClickListener#onMenuItemClick(android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		
		try {
			if (!this.privateBrowsing) {
				
				int id = webController.newTab(this.parent, false);
				webController.setCurrentTab(id);
				webController.goTo(url);
				
			} else {
				
				int id = webController.newTab(this.parent, true);
				webController.setCurrentTab(id);
				webController.goTo(url);
			}
			
		} catch (TooManyTabException e) {
			Toast.makeText(this.parent, "Too Many Tabs", Toast.LENGTH_LONG).show();
		}
		
		return true;
	}

}
