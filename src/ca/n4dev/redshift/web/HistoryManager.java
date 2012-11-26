/*
 * HistoryManager.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-11-25
 */ 
package ca.n4dev.redshift.web;

import java.util.ArrayList;
import java.util.List;


import android.util.Log;

public class HistoryManager {
	
	private static final String TAG = "BrowserActivity";
	
	private List<HistoryItem> history;
	private int idx = -1;
	
	public HistoryManager() {
		this(null);
	}
	
	public HistoryManager(List<HistoryItem> history) {
		if (history != null)
			this.history = history;
		else 
			this.history = new ArrayList<HistoryItem>();
	}

	public void add(String url, boolean isGoingTo) {
		Log.d(TAG, "Adding url: " + url);
		
		HistoryItem i = new HistoryItem(url, idx);
		this.history.add(i);
		
		if (isGoingTo) 
			this.idx = this.history.size()-1;
	}
	
	public String getPrevious(boolean isGoingTo) {
		Log.d(TAG, "Going back " + isGoingTo);
		
		HistoryItem i = getCurrent();
		int previous = i.getPreviousIdx();
		if (isGoingTo)
			this.idx = previous;
			
		return this.history.get(previous).getUrl();
	}
	
	public HistoryItem getCurrent() {
		return this.history.get(idx);
	}
	
	public boolean canGoBack() {
		if (this.idx > 0)
			return true;
		return false;
	}
}
