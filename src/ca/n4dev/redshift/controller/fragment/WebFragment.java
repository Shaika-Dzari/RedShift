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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.RenderPriority;

public class WebFragment extends Fragment {

	private static final String TAG = "WebFragment";
	private View view = null;
	private WebView webview;
	
	private Integer tabId;
	private boolean privateBrowsing;
	private String url;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "WebFragment onCreateView");
		
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_webcontainer, container, false);
			
			this.webview = (WebView) view.findViewById(R.id.fragment_webview);
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
			
			this.webview.setWebViewClient(new WebViewClient());
			
			if (this.url != null)
				this.webview.loadUrl(url);
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
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
}
