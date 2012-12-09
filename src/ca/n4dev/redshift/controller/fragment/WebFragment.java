/*
 * WebFragment.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-12-04
 */ 
package ca.n4dev.redshift.controller.fragment;

import ca.n4dev.redshift.R;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.RenderPriority;

public class WebFragment extends Fragment {

	private static final String TAG = "WebFragment";
	private View view = null;
	private WebView webview = null;
	private WebViewClient webViewClient = null;
	private WebChromeClient webChromeClient = null;
	
	private Bundle bundle = null;
	
	private Integer tabId;
	private boolean privateBrowsing;
	private String initialUrl;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "WebFragment onCreateView");
		
		if (view == null) {
			
			view = inflater.inflate(R.layout.fragment_webcontainer, container, false);
			
			this.webview = (WebView) view.findViewById(R.id.fragment_webview);
			
			if (bundle == null) {
				WebSettings settings = this.webview.getSettings();
				
				settings.setRenderPriority(RenderPriority.HIGH);
				settings.setJavaScriptEnabled(true);
				settings.setBuiltInZoomControls(true);
				settings.setDisplayZoomControls(false);
				settings.setGeolocationEnabled(false);
				settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
				settings.setUseWideViewPort(true);
				settings.setLoadWithOverviewMode(true);
				//settings.setUserAgentString("N4 Browser");
				
				this.webview.setWebViewClient(this.webViewClient);
				this.webview.setWebChromeClient(this.webChromeClient);
				
				if (this.initialUrl != null)
					this.webview.loadUrl(initialUrl);
			} else {
				
				this.webview.restoreState(bundle);
			}
		}
		
		return view;
	}

	/**
	 * @return the id
	 */
	public Integer getTabId() {
		return tabId;
	}

	/**
	 * @param id the id to set
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

	/**
	 * @return the url
	 */
	public String getInitialUrl() {
		return initialUrl;
	}

	/**
	 * @param url the url to set
	 */
	public void setInitialUrl(String initialUrl) {
		this.initialUrl = initialUrl;
	}

	/**
	 * @return the webview
	 */
	public WebView getWebview() {
		return webview;
	}

	/**
	 * @param webViewClient the webViewClient to set
	 */
	public void setWebViewClient(WebViewClient webViewClient) {
		this.webViewClient = webViewClient;
	}

	/**
	 * @param webChromeClient the webChromeClient to set
	 */
	public void setWebChromeClient(WebChromeClient webChromeClient) {
		this.webChromeClient = webChromeClient;
	}

	/**
	 * @return the bundle
	 */
	public Bundle getBundle() {
		return bundle;
	}

	/**
	 * @param bundle the bundle to set
	 */
	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}
}
