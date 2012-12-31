package ca.n4dev.redshift.controller.web;

import java.util.List;

import ca.n4dev.redshift.BrowserActivity;
import ca.n4dev.redshift.events.UrlModificationAware;
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
	
	public RsWebViewClient(UrlModificationAware urlModificationAware) {
		this.urlModificationAware = urlModificationAware;
	}
	
	@Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
		Log.d(TAG, url);

		if (url.contains("youtube.com")) {
			Log.d(TAG, "Trying to launch youtube app");
			Uri u = Uri.parse(url);
	    	String videoId = u.getQueryParameter("v");
	    	
	    	if (urlModificationAware.requestYoutubeOpening(videoId))
	    		return true;
	    	
		} else if (url.contains("play.google.com") || url.contains("market://")) {
			Log.d(TAG, "Trying to launch google play app");
			return true;
		} 
		
		this.urlModificationAware.urlHasChanged(url);
		view.loadUrl(url);
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
