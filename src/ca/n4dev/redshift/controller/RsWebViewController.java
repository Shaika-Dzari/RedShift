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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.widget.FrameLayout;
import ca.n4dev.redshift.SettingsActivity;
import ca.n4dev.redshift.controller.api.NavigationController;
import ca.n4dev.redshift.controller.api.TabController;
import ca.n4dev.redshift.controller.api.TooManyTabException;
import ca.n4dev.redshift.controller.api.WebController;
import ca.n4dev.redshift.controller.container.RsWebView;
import ca.n4dev.redshift.controller.web.RsWebViewClient;
import ca.n4dev.redshift.events.CloseAware;
import ca.n4dev.redshift.events.ProgressAware;
import ca.n4dev.redshift.events.UrlModificationAware;
import ca.n4dev.redshift.utils.UserAgent;

@SuppressLint({ "UseSparseArrays", "SetJavaScriptEnabled" })
public class RsWebViewController implements WebController, CloseAware {
	
	private static final String TAG = "RsWebViewController";
	public static final String HOME = "file:///android_asset/home.html";
	private static final int MAX_OPEN_TAB = 6;
	
	private CookieSyncManager syncManager;
	private CookieManager cookieManager;
	public static final String SAVE_KEY = "key";
	public static final String SAVE_WEBVIEW = "webview";
	public static final String SAVE_CURRENT = "current";
	
	
	private List<RsWebView> webviews;
	private int currentTabView;
	private int counter = 0;
	private UrlModificationAware urlModificationAware;
	private ProgressAware progressAware;
	private FrameLayout parentLayout;
	private Context context;
	
	
	
	private RsSettingsFactory settingsFactory = null;
	
	
	public RsWebViewController(Context context, FrameLayout parentLayout, UrlModificationAware urlModificationAware, ProgressAware progressAware) {
		this.parentLayout = parentLayout;
		this.urlModificationAware = urlModificationAware;
		this.progressAware = progressAware;
		this.context = context;
		this.webviews = new ArrayList<RsWebView>();

		this.settingsFactory = RsSettingsFactory.getInstance(context);
		this.settingsFactory.syncSettings();
		
		//this.withCookie = preferences.getBoolean("pref_cookie", true);
		//this.withJs = preferences.getBoolean("pref_js", true);
		/*
		syncManager = CookieSyncManager.createInstance(context);
		cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(this.prefCookie);
	
		if (this.prefCookie) {
			syncManager.startSync();
		}
		*/
	}
	
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

	@Override
	public void goTo(String url) {
		getCurrentView().loadUrl(url);
	}

	@Override
	public void refresh() {
		getCurrentView().reload();
	}

	@Override
	public int newTab(Activity activity, boolean privateBrowsing) throws TooManyTabException {
		
		if (this.webviews.size() + 1 > MAX_OPEN_TAB)
			throw new TooManyTabException();
		
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
										
									}, privateBrowsing);
		w.setTabId(id);
		settingsFactory.applySettings(w);
		
		activity.registerForContextMenu(w);
		
		this.webviews.add(w);
		
		return id;
	}

	@Override
	public List<RsWebView> listTab() {
		return this.webviews;
	}

	@Override
	public void closeTab(int tabId) {
		boolean hasRemoved = false;
		Iterator<RsWebView> it = this.webviews.iterator();
		
		while (it.hasNext() && !hasRemoved) {
			RsWebView r = it.next();
			if (r.getTabId() == tabId) {
				it.remove();
				hasRemoved = true;
			}
		}
		
		if (tabId == currentTabView && hasRemoved) {
			
			if (this.webviews.size() > 0) {
				currentTabView = this.webviews.get(0).getTabId();
				swapView(this.webviews.get(0));
			} else {
				currentTabView = -1;
			}
		}
		
	}

	@Override
	public void setCurrentTab(int tabId) {
		
		this.currentTabView = tabId;
		swapView();
	}

	public void saveState(Bundle outstate) {
		outstate.putInt("redshift.currentTabView", currentTabView);
		
		Iterator<RsWebView> it = this.webviews.iterator();
		RsWebView r = null;
		Bundle webviewBundles = new Bundle();
		Bundle b;
		
		while (it.hasNext()) {
			b = new Bundle();
			r = it.next();
			r.saveState(b);
			
			b.putInt("redshift.tabId", r.getTabId());
			
			webviewBundles.putBundle("redshift.www" + r.getTabId(), b);
		}
		
		outstate.putBundle("redshift.wwwBundle", webviewBundles);
	}

	public void restoreState(Bundle outstate) {
		currentTabView = outstate.getInt("redshift.currentTabView");
		
		Bundle webviewBundles = outstate.getBundle("redshift.wwwBundle");
		Bundle b;
		RsWebView w;
		
		for (String k : webviewBundles.keySet()) {
			if (k.startsWith("redshift.www")) {
				b = webviewBundles.getBundle(k);
				
				
				w = new RsWebView(context, 
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
							
						});
				w.setTabId(b.getInt("redshift.tabId"));
				w.restoreState(b);
				this.webviews.add(w);
			}
		}
		
	}
	
	private void swapView() {
		swapView(getCurrentView());
	}
	
	private void swapView(RsWebView view) {
		parentLayout.removeAllViews();
		parentLayout.addView(view);
	}
	
	private void notifyUrlChange(RsWebView w) {
		this.urlModificationAware.urlHasChanged(w.getUrl());
	}
	
	private RsWebView getCurrentView() {
		
		boolean hasFound = false;
		Iterator<RsWebView> it = this.webviews.iterator();
		RsWebView r = null;
		
		while (it.hasNext() && !hasFound) {
			r = it.next();
			if (r.getTabId() == currentTabView) {
				hasFound = true;
			}
		}
		
		return (hasFound) ? r : null;
	}
	
	public int currentId() {
		return currentTabView;
	}
	
	public String currentUrl() {
		return getCurrentView().getUrl();
	}
	
	public String currentTitle() {
		return getCurrentView().getTitle();
	}

	public boolean isCurrentTabPrivate() {
		return getCurrentView().isPrivateBrowsing();
	}
	
	
	public void pause() {
		//this.syncManager.stopSync();
	}
	
	public void resume() {
		//this.syncManager.startSync();
		//this.settingsFactory.syncSettings();
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.events.CloseAware#close()
	 */
	@Override
	public void close() {
		
	}


	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#clearCache()
	 */
	@Override
	public void clearCache() {
		for (RsWebView w : this.webviews) {
			w.clearCache(true);
		}
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#clearformData()
	 */
	@Override
	public void clearformData() {
		for (RsWebView w : this.webviews) {
			w.clearFormData();
		}
	}
}
