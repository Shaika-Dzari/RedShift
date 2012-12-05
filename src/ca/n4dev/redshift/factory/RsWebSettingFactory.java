/*
 * RsWebSettingFactory.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-12-02
 */ 
package ca.n4dev.redshift.factory;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.RenderPriority;

@Deprecated
public class RsWebSettingFactory {

	private Context context;
	private SharedPreferences preference;
	
	public RsWebSettingFactory(Context context) {
		this.context = context;
		this.preference = PreferenceManager.getDefaultSharedPreferences(this.context);
	}
	
	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.factory.api.WebSettingFactory#appliedSettings(android.webkit.WebView)
	 */
	
	public void appliedSettings(WebView w, boolean privateBrowsing) {
		WebSettings settings = w.getSettings();
		
		settings.setRenderPriority(RenderPriority.HIGH);
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        //settings.setDisplayZoomControls(false);
        settings.setGeolocationEnabled(false);
        settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
	}

}
