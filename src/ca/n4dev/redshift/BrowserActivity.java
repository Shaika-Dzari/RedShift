package ca.n4dev.redshift;



import ca.n4dev.redshift.R;
import ca.n4dev.redshift.web.HistoryManager;
import ca.n4dev.redshift.web.UrlUtils;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

@SuppressLint("SetJavaScriptEnabled")
public class BrowserActivity extends Activity {
	
	private static final String TAG = "BrowserActivity";
	
	private static final String HOME = "<html><body><div style='margin:0 auto;'><div style='witdh:400px;text-align:center;'><span style='font-weight:1.4em;'>DeltaV Web Browser</span></div></div></body></html>";
	
	private WebView n4webview;
	private HistoryManager historyManager = new HistoryManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        
        EditText txtUrl = (EditText) findViewById(R.id.txtUrl);
        txtUrl.setOnEditorActionListener(editTextListener);
        
        this.n4webview = (WebView) findViewById(R.id.webview);
        WebSettings settings = this.n4webview.getSettings();
        
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
        this.n4webview.loadUrl("http://www.lapresse.ca/");
        
    }
    
    
    //-------------------------------------------------------------------------
    // Private class
    //-------------------------------------------------------------------------
    private class N4WebViewClient extends WebViewClient {
    	@Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
    		Log.d(TAG, "UrlLoading [WebView]: " + url);
    		
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
				n4webview.loadUrl(url);
				
				return true;
			}
			return false;
		}
	};
	
	public void btnshowPopupSetting(View view) {
    	PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.activity_browser, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_browser, menu);
        return true;
    }
    
    
    //-------------------------------------------------------------------------
    // Window Events
    //-------------------------------------------------------------------------
    public void onBtnBack(View view) {
    	if (historyManager.canGoBack()) {
    		String url = historyManager.getPrevious(true);
    		//n4WebView.get
    		n4webview.loadUrl(url);
    	}
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && historyManager.canGoBack()) {
        	String url = historyManager.getPrevious(true);
    		//n4WebView.get
        	n4webview.loadUrl(url);
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
}
