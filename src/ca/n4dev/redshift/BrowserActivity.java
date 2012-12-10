package ca.n4dev.redshift;



import ca.n4dev.redshift.R;
import ca.n4dev.redshift.controller.RsWebController;
import ca.n4dev.redshift.controller.api.WebController;
import ca.n4dev.redshift.events.ProgressAware;
import ca.n4dev.redshift.events.UrlModificationAware;
import ca.n4dev.redshift.history.UrlUtils;
import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;


@SuppressLint("SetJavaScriptEnabled")
public class BrowserActivity extends FragmentActivity implements UrlModificationAware, ProgressAware {
	
	private static final String TAG = "BrowserActivity";
	
	private WebController webController;
	private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_browser);
        
        String initUrl = WebController.HOME;
        
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        
        if (data != null && action.equalsIgnoreCase("android.intent.action.VIEW")) {
        	initUrl = data.toString();
        }
        
        
        /*
        ImageButton b = (ImageButton) findViewById(R.id.btnSetting);
        registerForContextMenu(b);
        */
        progressBar = (ProgressBar) findViewById(R.id.browser_progressbar);
        
        EditText txt = (EditText) findViewById(R.id.txtUrl);
        txt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO) {
					
					String url = v.getText().toString();
					url = UrlUtils.sanitize(url);
					Log.d(TAG, "UrlLoading [Action]: " + url);
	
					webController.goTo(url);
					return true;
				}
				return false;
			}
		});
        
        
        if (this.webController == null) {
        	Log.d(TAG, "Creating WebController.");
        	this.webController = new RsWebController(R.id.layout_content, getFragmentManager(), this, this);
        	
        	if (savedInstanceState == null) {
        		int homeTab = this.webController.newTab(initUrl);
        		this.webController.setCurrentTab(homeTab);        		
        	} else {
        		this.webController.restoreState(savedInstanceState);
        	}
        	
        }
    }
    
    @Override
    protected void onSaveInstanceState (Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
    	this.webController.saveState(outState);
    	
    }
    
    public void onBtnShowPopup(View v) {
    	PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.activity_browser, popup.getMenu());
        
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				
				switch (item.getItemId()) {
					case R.id.menu_home:
						webController.goTo(WebController.HOME);
						return true;
					case R.id.menu_settings:
						startPreferenceActivity();
						return true;
				}
				
				return true;
			}
		});
        
        popup.show();
        
        //return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	return super.onOptionsItemSelected(item);
    }
    
    
    
    /* Activity Events */
    public void onBtnListTab(View v) {
    	PopupMenu popup = new PopupMenu(this, v);
    	popup.getMenuInflater().inflate(R.menu.menu_tab, popup.getMenu());
    	SparseArray<String> lst = this.webController.listTab();
    	int s = lst.size();
    	int key;
    	String url;

    	for (int i = 0; i < s; i++) {
    		key = lst.keyAt(i);
    		url = lst.get(key);
    		popup.getMenu().add(Menu.NONE, key, i, url);
    	}

    	//popup.getMenu().add(Menu.NONE, itemId, order, title)
    	
    	popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				int id = item.getItemId();
				webController.setCurrentTab(id);
				return true;
			}
		});
    	
    	popup.show();
    }
    
    public void onBtnNewTab(View v) {
    	int newTab = this.webController.newTab(WebController.HOME);
    	this.webController.setCurrentTab(newTab);
    }

	@Override
	public void urlHasChanged(String url) {
		EditText txt = (EditText) findViewById(R.id.txtUrl);
		txt.setText(url);
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

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.events.ProgressAware#hasProgressTo(int)
	 */
	@Override
	public void hasProgressTo(int progress) {
		
		if (progress < 100 && progressBar.getVisibility() == ProgressBar.INVISIBLE){
			progressBar.setVisibility(ProgressBar.VISIBLE);
        }
		
		progressBar.setProgress(progress);
		
        if(progress == 100) {
        	progressBar.setVisibility(ProgressBar.INVISIBLE);
        }
	}
	
	
	private void startPreferenceActivity() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
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
