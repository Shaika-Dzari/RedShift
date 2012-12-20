/*
 * RsWebView.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-12-18
 */ 
package ca.n4dev.redshift.controller.container;

import ca.n4dev.redshift.utils.Logger;
import android.annotation.SuppressLint;
import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.RenderPriority;


public class RsWebView extends WebView {

	private static final String TAG = "RsWebView";
	
	private Integer tabId;
	private boolean privateBrowsing;
	
	/**
	 * @param context
	 */
	@SuppressLint("SetJavaScriptEnabled")
	public RsWebView(Context context, WebViewClient webViewClient, WebChromeClient webChromeClient, boolean withJs) {
		super(context);
		Logger.log(TAG, "Creating new RsWebView");
		
		this.setWebChromeClient(webChromeClient);
		this.setWebViewClient(webViewClient);
		
		WebSettings settings = this.getSettings();
		settings.setRenderPriority(RenderPriority.HIGH);
		settings.setJavaScriptEnabled(withJs);
		settings.setBuiltInZoomControls(true);
		settings.setDisplayZoomControls(false);
		settings.setGeolocationEnabled(false);
		settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
		
		settings.setUserAgentString(settings.getUserAgentString() + " RedShift/1.0");
	}
	

	/**
	 * @return the tabId
	 */
	public Integer getTabId() {
		return tabId;
	}


	/**
	 * @param tabId the tabId to set
	 */
	public void setTabId(Integer tabId) {
		this.tabId = tabId;
	}


	/**
	 * @return the privateBrowsing
	 */
	public boolean isPrivateBrowsing() {
		return privateBrowsing;
	}


	/**
	 * @param privateBrowsing the privateBrowsing to set
	 */
	public void setPrivateBrowsing(boolean privateBrowsing) {
		this.privateBrowsing = privateBrowsing;
	}

}
