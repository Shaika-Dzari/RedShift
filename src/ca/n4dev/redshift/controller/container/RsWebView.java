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

import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class RsWebView extends WebView {
	private Integer tabId;
	private boolean privateBrowsing;
	
	
	public RsWebView(Context context, WebViewClient webViewClient, WebChromeClient webChromeClient) {
		this(context, webViewClient, webChromeClient, false);
	}
	
	public RsWebView(Context context, WebViewClient webViewClient, WebChromeClient webChromeClient, boolean privateBrowsing) {
		//super(context);
		super(context, null, android.R.attr.webViewStyle, privateBrowsing);
		
		this.privateBrowsing = privateBrowsing;		
		this.setWebChromeClient(webChromeClient);
		this.setWebViewClient(webViewClient);
		
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
