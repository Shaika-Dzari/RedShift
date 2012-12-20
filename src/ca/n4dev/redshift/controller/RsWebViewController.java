/*
 * RsWebViewController.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-12-19
 */ 
package ca.n4dev.redshift.controller;


import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import ca.n4dev.redshift.R;
import ca.n4dev.redshift.controller.api.WebController;
import ca.n4dev.redshift.controller.container.RsWebView;
import ca.n4dev.redshift.controller.web.RsWebViewClient;
import ca.n4dev.redshift.events.ProgressAware;
import ca.n4dev.redshift.events.UrlModificationAware;

public class RsWebViewController implements WebController {
	
	private static final String TAG = "RsWebViewController";
	
	private CookieSyncManager syncManager;
	private CookieManager cookieManager;
	public static final String SAVE_KEY = "key";
	public static final String SAVE_WEBVIEW = "webview";
	public static final String SAVE_CURRENT = "current";
	
	
	private SparseArray<RsWebView> webviews;
	private int currentTabView;
	private int counter = 0;
	private UrlModificationAware urlModificationAware;
	private ProgressAware progressAware;
	private FrameLayout parentLayout;
	private boolean initialFragment = true;
	private Context context;
	private SharedPreferences preferences;
	
	private boolean withCookie;
	private boolean withJs;
	
	
	
	public RsWebViewController(Context context, FrameLayout parentLayout, UrlModificationAware urlModificationAware, ProgressAware progressAware) {
		this.parentLayout = parentLayout;
		this.urlModificationAware = urlModificationAware;
		this.progressAware = progressAware;
		this.context = context;
		this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
		this.withCookie = preferences.getBoolean("pref_cookie", true);
		this.withJs = preferences.getBoolean("pref_js", true);
		this.webviews = new SparseArray<RsWebView>();
		
		syncManager = CookieSyncManager.createInstance(context);
		cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(this.withCookie);
	
		if (this.withCookie) {
			syncManager.startSync();
		}
		
	}
	
	
	

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#goBack()
	 */
	@Override
	public boolean goBack() {
		RsWebView w = getCurrentView();
		if (w.canGoBack()) {
			w.goBack();
			notifyUrlChange(w);
			return true;
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#goForward()
	 */
	@Override
	public boolean goForward() {
		RsWebView w = getCurrentView();
		if (w.canGoForward()) {
			w.goForward();
			notifyUrlChange(w);
			return true;
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#goTo(java.lang.String)
	 */
	@Override
	public void goTo(String url) {
		getCurrentView().loadUrl(url);
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#refresh()
	 */
	@Override
	public void refresh() {
		getCurrentView().reload();
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#currentUrl()
	 */
	@Override
	public String currentUrl() {
		return getCurrentView().getUrl();
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#newTab(java.lang.String)
	 */
	@Override
	public int newTab() {
		int id = ++counter;
		RsWebView w = new RsWebView(context, 
									new RsWebViewClient(this.urlModificationAware), 
									new WebChromeClient() {
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
										
									}, 
									withJs);
		w.setTabId(id);
		
		this.webviews.append(id, w);
		
		return id;
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#listTab()
	 */
	@Override
	public SparseArray<String> listTab() {
		SparseArray<String> lst = new SparseArray<String>();
		for (int i = 0; i < this.webviews.size(); i++) {
			lst.append(this.webviews.keyAt(i), this.webviews.valueAt(i).getUrl());
		}
		return lst;
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#closeTab(int)
	 */
	@Override
	public void closeTab(int tabId) {
		this.webviews.delete(tabId);
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#setCurrentTab(int)
	 */
	@Override
	public void setCurrentTab(int tabId) {
		this.currentTabView = tabId;
		swapView();
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#saveState(android.os.Bundle)
	 */
	@Override
	public void saveState(Bundle outstate) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#restoreState(android.os.Bundle)
	 */
	@Override
	public void restoreState(Bundle outstate) {
		// TODO Auto-generated method stub

	}
	
	private void swapView() {
		parentLayout.removeAllViews();
		parentLayout.addView(getCurrentView());
	}
	
	private void notifyUrlChange(RsWebView w) {
		this.urlModificationAware.urlHasChanged(w.getUrl());
	}
	
	private RsWebView getCurrentView() {
		return this.webviews.get(currentTabView);
	}

}
