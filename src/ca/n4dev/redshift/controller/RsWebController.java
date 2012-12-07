/*
 * RsWebController.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-12-04
 */ 
package ca.n4dev.redshift.controller;

import java.util.HashMap;
import java.util.Map;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import ca.n4dev.redshift.controller.api.WebController;
import ca.n4dev.redshift.controller.fragment.WebFragment;

public class RsWebController implements WebController {
	
	private static final String TAG = "RsWebController";
	
	private Map<Integer, WebFragment> webviews;
	private int currentTabView;
	private int counter = 0;
	private FragmentManager fragmentManager;
	private int layoutId;
	
	public RsWebController(int layoutId, FragmentManager fragmentManager) {
		this.webviews = new HashMap<Integer, WebFragment>();
		this.layoutId = layoutId;
		this.fragmentManager = fragmentManager;
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#goBack()
	 */
	@Override
	public boolean goBack() {
		if (getCurrentWebview().getWebview().canGoBack()) {
			getCurrentWebview().getWebview().goBack();
			return true;
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#goForward()
	 */
	@Override
	public boolean goForward() {
		if (getCurrentWebview().getWebview().canGoForward()) {
			getCurrentWebview().getWebview().goForward();
			return true;
		}
			
		return false;
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#goTo(java.lang.String)
	 */
	@Override
	public void goTo(String url) {
		getCurrentWebview().setUrl(url);
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#refresh()
	 */
	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#newTab()
	 */
	@Override
	public int newTab() {
		Log.d(TAG, "Creating a new tab");
		int id = ++counter;
		WebFragment wf = new WebFragment();
		wf.setTabId(id);
		this.webviews.put(id, wf);
		
		return id;
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#closeTab()
	 */
	@Override
	public void closeTab(int tabId) {
		if (this.webviews.containsKey(tabId)) {
			this.webviews.remove(tabId);
		}
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#setCurrentTab(java.lang.String)
	 */
	@Override
	public void setCurrentTab(int tabId) {
		this.currentTabView = tabId;
		changeWebFragment();
	}
	
	private void changeWebFragment() {
		FragmentTransaction trx = this.fragmentManager.beginTransaction();
		trx.add(layoutId, getCurrentWebview());
		trx.commit();
	}
	
	private WebFragment getCurrentWebview() {
		return this.webviews.get(this.currentTabView);
	}
}
