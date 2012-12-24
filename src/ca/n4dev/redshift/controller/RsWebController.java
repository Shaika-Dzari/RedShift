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
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import ca.n4dev.redshift.controller.api.WebController;
import ca.n4dev.redshift.controller.container.RsWebView;
import ca.n4dev.redshift.controller.fragment.WebFragment;
import ca.n4dev.redshift.controller.web.RsWebViewClient;
import ca.n4dev.redshift.events.ProgressAware;
import ca.n4dev.redshift.events.UrlModificationAware;

@SuppressLint("UseSparseArrays")
public class RsWebController implements WebController {
	
	private static final String TAG = "RsWebController";
	public static final String SAVE_KEY = "key";
	public static final String SAVE_WEBVIEW = "webview";
	public static final String SAVE_CURRENT = "current";
	
	private Map<Integer, WebFragment> webviews;
	private int currentTabView;
	private int counter = 0;
	private FragmentManager fragmentManager;
	private UrlModificationAware urlModificationAware;
	private ProgressAware progressAware;
	private int layoutId;
	private boolean initialFragment = true;
	
	public RsWebController(int layoutId, FragmentManager fragmentManager, UrlModificationAware urlModificationAware, ProgressAware progressAware) {
		this.webviews = new HashMap<Integer, WebFragment>();
		this.layoutId = layoutId;
		this.fragmentManager = fragmentManager;
		this.urlModificationAware = urlModificationAware;
		this.progressAware = progressAware;
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#goBack()
	 */
	@Override
	public boolean goBack() {
		WebFragment wf = getCurrentWebview();
		if (wf.getWebview().canGoBack()) {
			wf.getWebview().goBack();
			notifyUrlChange(wf);
			return true;
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#goForward()
	 */
	@Override
	public boolean goForward() {
		WebFragment wf = getCurrentWebview();
		if (wf.getWebview().canGoForward()) {
			wf.getWebview().goForward();
			notifyUrlChange(wf);
			return true;
		}
			
		return false;
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#goTo(java.lang.String)
	 */
	@Override
	public void goTo(String url) {
		getCurrentWebview().getWebview().loadUrl(url);
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#refresh()
	 */
	@Override
	public void refresh() {
		getCurrentWebview().getWebview().reload();
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
		wf.setInitialUrl(HOME);
		wf.setWebViewClient(new RsWebViewClient(this.urlModificationAware));
		wf.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
				progressAware.hasProgressTo(progress);
			}
			
			@Override
            public void onReceivedTitle(WebView view, String title) {
				Log.d("WebChromeClient", "title: " + title);
				Log.d("WebChromeClient", "url" + view.getUrl());
				
				urlModificationAware.pageReceived(view.getUrl(), title);
				
			}
			
		});
		
		
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
		WebFragment wf = getCurrentWebview();
		FragmentTransaction trx = this.fragmentManager.beginTransaction();
		
		if (initialFragment) {
			
			trx.add(layoutId, wf);	
			initialFragment = false;
		} else {
			trx.replace(layoutId, wf);
		}
		
		trx.commit();
		notifyUrlChange(wf);
	}
	
	private WebFragment getCurrentWebview() {
		return this.webviews.get(this.currentTabView);
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#listTab()
	 */
	@Override
	public List<RsWebView> listTab() {
		
		return null;
	}
	
	
	private void notifyUrlChange(WebFragment wf) {
		// Update url
		if (wf.getWebview() == null)
			this.urlModificationAware.urlHasChanged(getCurrentWebview().getInitialUrl());
		else 
			this.urlModificationAware.urlHasChanged(getCurrentWebview().getWebview().getUrl());
	}
	
	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#saveState(android.os.Bundle)
	 */
	public void saveState(Bundle outState) {
		Bundle b;
		int i = 0;
		
		outState.putInt(SAVE_CURRENT, this.currentTabView);
		
		int[] bundleKey = new int[this.webviews.size()];
		
		for (int k : this.webviews.keySet()) {
			bundleKey[i] = k;
			
			b = new Bundle();
			//b.putInt(SAVE_KEY, k);
			this.webviews.get(k).getWebview().saveState(b);
			
			outState.putBundle(SAVE_WEBVIEW + "_" + k, b);
			i++;
		}
		
		outState.putIntArray(SAVE_KEY, bundleKey);
		
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#restoreState(android.os.Bundle)
	 */
	@Override
	public void restoreState(Bundle outstate) {
		// Get keys
		int[] keys = outstate.getIntArray(SAVE_KEY);
		this.currentTabView = outstate.getInt(SAVE_CURRENT);
		
		for (int k : keys) {
			WebFragment wf = new WebFragment();
			Bundle b = outstate.getBundle(SAVE_WEBVIEW + "_" + k);
			wf.setTabId(k);
			
			wf.setBundle(b);
			
			this.webviews.put(k, wf);
		}
		
		//outstate.ge
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#currentUrl()
	 */
	@Override
	public String currentUrl() {
		
		WebFragment wf = getCurrentWebview();
		if (wf.getWebview() == null)
			return wf.getInitialUrl();
		else 
			return wf.getWebview().getUrl();
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#currentId()
	 */
	@Override
	public int currentId() {
		return this.currentTabView;
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#currentTitle()
	 */
	@Override
	public String currentTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#loadWebSettings()
	 */
	@Override
	public void loadWebSettings() {
		// TODO Auto-generated method stub
		
	}
	
}
