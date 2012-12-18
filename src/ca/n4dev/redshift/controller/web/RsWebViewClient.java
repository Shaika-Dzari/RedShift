package ca.n4dev.redshift.controller.web;

import ca.n4dev.redshift.events.UrlModificationAware;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class RsWebViewClient extends WebViewClient {

	private static final String TAG = "RsWebViewClient";
	
	private UrlModificationAware urlModificationAware;
	private boolean loadingPage = false;
	private boolean redirect = false;
	
	public RsWebViewClient(UrlModificationAware urlModificationAware) {
		this.urlModificationAware = urlModificationAware;
	}
	
	@Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
		Log.d(TAG, "shouldOverrideUrlLoading: " + url);
		this.urlModificationAware.urlHasChanged(url);
		view.loadUrl(url);
		return true;
	}
	
	@Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
		loadingPage = true;
		Log.d(TAG, "onPageStarted: " + url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
    	loadingPage = false;
    	Log.d(TAG, "onPageFinished: " + url);
    }
}
