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
import ca.n4dev.redshift.controller.api.WebController;
import ca.n4dev.redshift.controller.container.RsWebView;
import ca.n4dev.redshift.controller.web.RsWebViewClient;
import ca.n4dev.redshift.events.ProgressAware;
import ca.n4dev.redshift.events.UrlModificationAware;
import ca.n4dev.redshift.utils.UserAgent;

@SuppressLint({ "UseSparseArrays", "SetJavaScriptEnabled" })
public class RsWebViewController implements WebController {
	
	private static final String TAG = "RsWebViewController";
	private static final int MAX_OPEN_TAB = 5;
	
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
	
	// Web Preferences
	private SharedPreferences preferences;
	private boolean prefCookie;
	private boolean prefJavascript;
	private boolean prefFormdata;
	private boolean prefSavePasswd;
	private boolean prefLoadImage;
	private WebSettings.PluginState prefPlugin;
	private UserAgent prefUserAgent;
	
	
	/*
	 public static final String KEY_HISTORY = "pref_history"; 
	public static final String KEY_COOKIE = "pref_cookie"; 
	public static final String KEY_COOKIEEXIT = "pref_cookie_exit"; 
	public static final String KEY_FORMDATA = "pref_save_formdata"; 
	public static final String KEY_SAVEPASSWD = "pref_save_passwd"; 
	public static final String KEY_JAVASCRIPT = "pref_js"; 
	public static final String KEY_LOADIMAGE = "pref_image"; 
	public static final String KEY_PLUGIN = "pref_plugin"; 
	 */
	
	
	
	public RsWebViewController(Context context, FrameLayout parentLayout, UrlModificationAware urlModificationAware, ProgressAware progressAware) {
		this.parentLayout = parentLayout;
		this.urlModificationAware = urlModificationAware;
		this.progressAware = progressAware;
		this.context = context;
		this.webviews = new ArrayList<RsWebView>();
		this.preferences = PreferenceManager.getDefaultSharedPreferences(context);

		loadWebSettings();
		
		//this.withCookie = preferences.getBoolean("pref_cookie", true);
		//this.withJs = preferences.getBoolean("pref_js", true);
		
		syncManager = CookieSyncManager.createInstance(context);
		cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(this.prefCookie);
	
		if (this.prefCookie) {
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
										
									});
		w.setTabId(id);
		setupWebSettings(w);
		
		this.webviews.add(w);
		
		return id;
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#listTab()
	 */
	@Override
	public List<RsWebView> listTab() {
		return this.webviews;
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#closeTab(int)
	 */
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

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#restoreState(android.os.Bundle)
	 */
	@Override
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
	

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#currentId()
	 */
	@Override
	public int currentId() {
		return currentTabView;
	}
	
	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#currentUrl()
	 */
	@Override
	public String currentUrl() {
		return getCurrentView().getUrl();
	}
	
	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#currentUrl()
	 */
	@Override
	public String currentTitle() {
		return getCurrentView().getTitle();
	}




	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.controller.api.WebController#loadWebSettings()
	 */
	@Override
	public void loadWebSettings() {
		/*
		 * private SharedPreferences preferences;
	private boolean prefCookie;
	private boolean prefJavascript;
	private boolean prefFormdata;
	private boolean prefSavePasswd;
	private boolean prefLoadImage;
	private boolean prefPlugin;
	
	
	 public static final String KEY_HISTORY = "pref_history"; 
	public static final String KEY_COOKIE = "pref_cookie"; 
	public static final String KEY_COOKIEEXIT = "pref_cookie_exit"; 
	public static final String KEY_FORMDATA = "pref_save_formdata"; 
	public static final String KEY_SAVEPASSWD = "pref_save_passwd"; 
	public static final String KEY_JAVASCRIPT = "pref_js"; 
	public static final String KEY_LOADIMAGE = "pref_image"; 
	public static final String KEY_PLUGIN = "pref_plugin"; 
	
	
	*/
		if (this.preferences != null) {
			prefCookie = preferences.getBoolean(SettingsActivity.KEY_COOKIE, true);
			prefJavascript = preferences.getBoolean(SettingsActivity.KEY_JAVASCRIPT, true);
			prefFormdata = preferences.getBoolean(SettingsActivity.KEY_FORMDATA, true);
			prefSavePasswd = preferences.getBoolean(SettingsActivity.KEY_SAVEPASSWD, true);
			prefLoadImage = preferences.getBoolean(SettingsActivity.KEY_LOADIMAGE, true);
			
			// Plug-ins
			String plugin = preferences.getString(SettingsActivity.KEY_PLUGIN, "On Demand");
			
			if (plugin.equalsIgnoreCase("on demand")) {
				prefPlugin = WebSettings.PluginState.ON_DEMAND;
			} else if (plugin.equalsIgnoreCase("enable")) {
				prefPlugin = WebSettings.PluginState.ON;
			} else {
				prefPlugin = WebSettings.PluginState.OFF;
			}
			
			// User Agent
			prefUserAgent = UserAgent.from(preferences.getString(SettingsActivity.KEY_USERAGENT, "Android"));
			
		}
	}
	
	private void setupWebSettings(WebView view) {
		WebSettings settings = view.getSettings();
		settings.setRenderPriority(RenderPriority.HIGH);
		settings.setJavaScriptEnabled(prefJavascript);
		settings.setBuiltInZoomControls(true);
		settings.setDisplayZoomControls(false);
		settings.setGeolocationEnabled(false);
		settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
		settings.setLoadsImagesAutomatically(prefLoadImage);
		settings.setPluginState(prefPlugin);
		settings.setSaveFormData(prefFormdata);
		settings.setSavePassword(prefSavePasswd);
		
		if (prefUserAgent == UserAgent.ANDROID)
			settings.setUserAgentString(settings.getUserAgentString() + " " + UserAgent.ANDROID.getString());
		else if (prefUserAgent == UserAgent.DESKTOP) {
			String u = settings.getUserAgentString();
			u = u.replace("Mobile", "") + " " + UserAgent.ANDROID.getString();
			settings.setUserAgentString(u);
		} else {
			settings.setUserAgentString(prefUserAgent.getString());
		}
		
	}
	
	public void stopCookieSync() {
		this.syncManager.stopSync();
	}
	
	public void startCookieSync() {
		this.syncManager.startSync();
	}
}
