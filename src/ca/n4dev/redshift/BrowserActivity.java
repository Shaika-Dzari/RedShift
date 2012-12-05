package ca.n4dev.redshift;



import ca.n4dev.redshift.R;
import ca.n4dev.redshift.controller.RsWebController;
import ca.n4dev.redshift.controller.api.WebController;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentActivity;


@SuppressLint("SetJavaScriptEnabled")
public class BrowserActivity extends FragmentActivity {
	
	private static final String TAG = "BrowserActivity";
	
	private WebController webController;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_browser);
        
        if (this.webController == null) {
        	
        	Log.d(TAG, "Creating WebController.");
        	this.webController = new RsWebController(R.id.fragment_webview, getSupportFragmentManager());
        	int homeTab = this.webController.newTab();
        	this.webController.setCurrentTab(homeTab);
        	this.webController.goTo("file:///android_asset/home.html");
        }
        
        
        /*
        EditText txtUrl = (EditText) findViewById(R.id.txtUrl);
        txtUrl.setOnEditorActionListener(editTextListener);
        
        this.n4webview = (WebView) findViewById(R.id.webview);
        WebSettings settings = this.n4webview.getSettings();
        
        settings.setRenderPriority(RenderPriority.HIGH);
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setGeolocationEnabled(false);
        settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        //settings.setUserAgentString("N4 Browser");
        
        this.n4webview.setWebViewClient(new N4WebViewClient());
        
        //this.n4webview.loadData(HOME, "text/html", null);
        updateUrlAndGo("file:///android_asset/home.html", true);
        */
        
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_browser, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	return super.onOptionsItemSelected(item);
    }
    
    
    //-------------------------------------------------------------------------
    // Private class
    //-------------------------------------------------------------------------
    /*
    private class N4WebViewClient extends WebViewClient {
    	@Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
    		Log.d(TAG, "UrlLoading [WebView]: " + url);
    		updateUrlAndGo(url, true);
    		historyManager.add(url, true);
    		n4webview.loadUrl(url);
            return true;
        }
    }
   
    
    
    
    private OnEditorActionListener editTextListener = new TextView.OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_GO) {
				String url = v.getText().toString();
				url = UrlUtils.sanitize(url);
				Log.d(TAG, "UrlLoading [Action]: " + url);

				historyManager.add(url, true);
				
				// Go To
				//n4webview.loadUrl(url);
				updateUrlAndGo(url, true);
				return true;
			}
			return false;
		}
	};
	
	
	public void btnshowPopupSetting(View view) {
    	PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.activity_browser, popup.getMenu());
        popup.show();
    }
    
    //-------------------------------------------------------------------------
    // Window Events
    //-------------------------------------------------------------------------
    public void onBtnBack(View view) {
    	
    	if (this.n4webview.canGoBack()) {
    		this.n4webview.goBack();
    		updateUrlAndGo(this.n4webview.getOriginalUrl(), false);
    		return;
    	}
    	
    	onBackPressed();
    	
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && historyManager.canGoBack()) {
        	
        	if (this.n4webview.canGoBack()) {
        		this.n4webview.goBack();
        		return true;
        	}
        	
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
   
    
    //
    private void updateUrlAndGo(String url, boolean loadIt) {
    	EditText e = (EditText) findViewById(R.id.txtUrl);
    	e.setText(url);
    	if (loadIt)
    		this.n4webview.loadUrl(url);
    }

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
        case R.id.menu_home:
        	updateUrlAndGo("file:///android_asset/home.html", true);
            return true;
        case R.id.menu_settings:
            
            return true;
        default:
            return super.onOptionsItemSelected(item);
    	}
	}
     */
}
