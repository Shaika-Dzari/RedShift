/*
 * WebSettings.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-12-26
 */ 
package ca.n4dev.redshift.controller;

import ca.n4dev.redshift.SettingsActivity;
import ca.n4dev.redshift.controller.api.WebController;
import ca.n4dev.redshift.settings.SettingsKeys;
import ca.n4dev.redshift.utils.UserAgent;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Browser;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebIconDatabase;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewDatabase;
import android.webkit.WebSettings.RenderPriority;

@SuppressLint("SetJavaScriptEnabled")
public class RsSettingsFactory {
	
	private static RsSettingsFactory instance = null;
	public static final String REDSHIFT_HOMEPAGE = "redshift:home";

	// Web Preferences
	private SharedPreferences preferences;
	private boolean prefCookie;
	private boolean prefCookieOnExit;
	private boolean prefJavascript;
	private boolean prefFormdata;
	private boolean prefSavePasswd;
	private boolean prefLoadImage;
	private WebSettings.PluginState prefPlugin;
	private UserAgent prefUserAgent;
	private Context context;
	private WebController webController;
	private CookieSyncManager syncManager;
	private String prefWebHome;
		
	private RsSettingsFactory(Context context) {
		this.context = context.getApplicationContext();
		this.preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
	}
	
	public static RsSettingsFactory getInstance(final Context context) {
		if (instance == null) {
			if (context == null)
				throw new NullPointerException("Context is null");
			instance = new RsSettingsFactory(context);
		}
		return instance;
	}
	
	public void applySettings(WebView webview) {
		
		WebSettings settings = webview.getSettings();
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
		settings.setAllowFileAccess(false);
		settings.setEnableSmoothTransition(true);
		
		// HTML5 API flags
        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCachePath(context.getDir("appcache", 0).getPath());
        settings.setDatabasePath(context.getDir("databases", 0).getPath());
		
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
	
	public void syncSettings() {
		
		if (this.preferences != null) {
			prefCookie = preferences.getBoolean(SettingsKeys.KEY_COOKIE, true);
			prefCookieOnExit = preferences.getBoolean(SettingsKeys.KEY_COOKIEEXIT, true);
			prefJavascript = preferences.getBoolean(SettingsKeys.KEY_JAVASCRIPT, true);
			prefFormdata = preferences.getBoolean(SettingsKeys.KEY_FORMDATA, true);
			prefSavePasswd = preferences.getBoolean(SettingsKeys.KEY_SAVEPASSWD, true);
			prefLoadImage = preferences.getBoolean(SettingsKeys.KEY_LOADIMAGE, true);
			prefWebHome = preferences.getString(SettingsKeys.KEY_HOMEPAGE, REDSHIFT_HOMEPAGE);
			
			// Plug-ins
			String plugin = preferences.getString(SettingsKeys.KEY_PLUGIN, "On Demand");
			
			if (plugin.equalsIgnoreCase("on demand")) {
				prefPlugin = WebSettings.PluginState.ON_DEMAND;
			} else if (plugin.equalsIgnoreCase("enable")) {
				prefPlugin = WebSettings.PluginState.ON;
			} else {
				prefPlugin = WebSettings.PluginState.OFF;
			}
			
			// User Agent
			prefUserAgent = UserAgent.from(preferences.getString(SettingsKeys.KEY_USERAGENT, "Android"));
			
			// Cookie 
			if (this.syncManager == null)
				syncManager = CookieSyncManager.createInstance(context);
			
			CookieManager.getInstance().setAcceptCookie(prefCookie);
			
			if (prefCookie)
				syncManager.startSync();
		}
	}

	public void clearCache() {
        WebIconDatabase.getInstance().removeAllIcons();
        if (webController!= null) {
        	webController.clearCache(); 
        }
    }
	
	public void clearCookies() {
        CookieManager.getInstance().removeAllCookie();
    }

    public void clearFormData() {
        WebViewDatabase.getInstance(this.context).clearFormData();
        if (webController!= null) {
        	webController.clearformData();
        }
    }

    public void clearPasswords() {
        WebViewDatabase db = WebViewDatabase.getInstance(this.context);
        db.clearUsernamePassword();
        db.clearHttpAuthUsernamePassword();
    }

    public void clearDatabases() {
        WebStorage.getInstance().deleteAllData();
    }

	
	/**
	 * @return the webController
	 */
	public WebController getWebController() {
		return webController;
	}

	/**
	 * @param webController the webController to set
	 */
	public void setWebController(WebController webController) {
		this.webController = webController;
	}

	/**
	 * @return the prefWebHome
	 */
	public String getPrefWebHome() {
		return prefWebHome;
	}
	
	public void pause() {
		this.syncManager.stopSync();
	}
	
	public void resume() {
		syncSettings();
	}
	
}
