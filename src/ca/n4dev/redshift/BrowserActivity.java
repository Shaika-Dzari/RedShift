package ca.n4dev.redshift;



import ca.n4dev.redshift.R;
import ca.n4dev.redshift.controller.RsWebController;
import ca.n4dev.redshift.controller.api.WebController;
import ca.n4dev.redshift.events.UrlModificationAware;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.support.v4.app.FragmentActivity;


@SuppressLint("SetJavaScriptEnabled")
public class BrowserActivity extends FragmentActivity implements UrlModificationAware {
	
	private static final String TAG = "BrowserActivity";
	
	private WebController webController;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_browser);
        
        ImageButton b = (ImageButton) findViewById(R.id.btnSetting);
        registerForContextMenu(b);
        
        
        if (this.webController == null) {
        	Log.d(TAG, "Creating WebController.");
        	this.webController = new RsWebController(R.id.layout_content, getSupportFragmentManager());
        	int homeTab = this.webController.newTab();
        	this.webController.setCurrentTab(homeTab);
        	this.webController.goTo("file:///android_asset/home.html");
        }
    }
    
    public void showPopup(View v) {
    	PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.activity_browser, popup.getMenu());
        popup.show();
        
        //return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	return super.onOptionsItemSelected(item);
    }
    
    
    
    /* Activity Events */
    public void onBtnListTab(View v) {
    	
    }
    
    public void onBtnNewTab(View v) {
    	
    }

	@Override
	public void urlHasChanged(String url) {
		// TODO Auto-generated method stub
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	
        	if (this.webController.goBack())
        		return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
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
