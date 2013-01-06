package ca.n4dev.redshift.controller.web;

import java.util.List;

import ca.n4dev.redshift.BrowserActivity;
import ca.n4dev.redshift.events.UrlModificationAware;
import ca.n4dev.redshift.utils.SpecialUrl;
import ca.n4dev.redshift.utils.UrlType;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class RsWebViewClient extends WebViewClient {

	private static final String TAG = "RsWebViewClient";
	
	private UrlModificationAware urlModificationAware;
	private SpecialUrl specialUrlManager;
	
	public RsWebViewClient(UrlModificationAware urlModificationAware, SpecialUrl specialUrlManager) {
		this.urlModificationAware = urlModificationAware;
		this.specialUrlManager = specialUrlManager;
	}
	
	@Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
		Log.d(TAG, url);
		UrlType type = this.specialUrlManager.getUrlType(url);
		if (type == UrlType.HTTP) {
			this.urlModificationAware.urlHasChanged(url);
			view.loadUrl(url);
			
		} else {
			this.specialUrlManager.overrideUrlIntent(type, url);
		}
		
		return true;
	}
	
	@Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
		Log.d(TAG, "onPageStarted: " + url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
    	Log.d(TAG, "onPageFinished: " + url);
    }
    
}
