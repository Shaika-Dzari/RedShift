package ca.n4dev.redshift;

import ca.n4dev.redshift.R;
import ca.n4dev.redshift.controller.RsWebViewController;
import ca.n4dev.redshift.controller.api.WebController;
import ca.n4dev.redshift.controller.container.RsWebView;
import ca.n4dev.redshift.events.ProgressAware;
import ca.n4dev.redshift.events.UrlModificationAware;
import ca.n4dev.redshift.history.HistoryDbHelper;
import ca.n4dev.redshift.utils.TabListAdapter;
import ca.n4dev.redshift.utils.UrlUtils;
import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;


@SuppressLint("SetJavaScriptEnabled")
public class BrowserActivity extends FragmentActivity implements UrlModificationAware, ProgressAware {
	
	private static final String TAG = "BrowserActivity";
	private static final int BOOKMARK_RESULT_ID = 9990;
	private static final int HISTORY_RESULT_ID = 9991;
	
	private WebController webController;
	private ProgressBar progressBar;
	private HistoryDbHelper historyHelper;
	private SQLiteDatabase historyDatabase;
	private BrowserActivity browserActivity;
	private ListView tabList = null;
	private LinearLayout tabLayout = null;
	
	private boolean tabLayoutVisible = false;
	
	private enum LayoutVisibility {
		VISIBLE, GONE, TOGGLE
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        browserActivity = this;
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_browser);
        
        historyHelper = new HistoryDbHelper(this);
        historyDatabase = historyHelper.getWritableDatabase();
        
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
        	//this.webController = new RsWebController(R.id.layout_content, getFragmentManager(), this, this);
        	this.webController = new RsWebViewController(this, (FrameLayout) findViewById(R.id.layout_content), this, this);
        	
        	if (savedInstanceState == null) {
        		int homeTab = this.webController.newTab();
        		this.webController.setCurrentTab(homeTab);        	
        		this.webController.goTo(initUrl);
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
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	String url = null;
    	if (data != null) {
    		if (requestCode == BOOKMARK_RESULT_ID || requestCode == HISTORY_RESULT_ID) {
    			url = data.getStringExtra("url");
    		
	    		if (url != null && !"".equals(url)) {
	    			urlHasChanged(url);
	    			webController.goTo(url);
	    		}
    		}
    	}
    }
    
    public void onBtnShowPopup(View v) {
    	PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.activity_browser, popup.getMenu());
        
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent;
				
				switch (item.getItemId()) {
					
					case R.id.menu_settings:
						startPreferenceActivity();
						break;
					case R.id.menu_bookmark:
						intent = new Intent(browserActivity, BookmarkActivity.class);
				    	startActivityForResult(intent, BOOKMARK_RESULT_ID);
						break;
						
					case R.id.menu_history:
						intent = new Intent(browserActivity, HistoryActivity.class);
				    	startActivityForResult(intent, HISTORY_RESULT_ID);
						break;
						
			        case R.id.menu_quit:
			        	finish();
			        	break;
			 
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
    	
    	if (tabList == null) {
    		
    		tabList = (ListView) findViewById(R.id.list_tab);
    		tabLayout = (LinearLayout) findViewById(R.id.layout_tabmenu);
    		
    		tabList.setAdapter(new TabListAdapter(this, this.webController.listTab()));
    		tabList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position,	long id) {
					RsWebView r = (RsWebView) tabList.getItemAtPosition(position);
					webController.setCurrentTab(r.getTabId());
					toggleTabLayout(LayoutVisibility.GONE);
				}
    			
    		});
    		
    	}
    	
    	((ArrayAdapter)tabList.getAdapter()).notifyDataSetChanged();
    	
    	toggleTabLayout(LayoutVisibility.TOGGLE);
    }
    
    
    
    public void onBtnNewTab(View v) {
    	int newTab = this.webController.newTab();
    	this.webController.setCurrentTab(newTab);
    	this.webController.goTo(WebController.HOME);
    	((ArrayAdapter)tabList.getAdapter()).notifyDataSetChanged();
    	toggleTabLayout(LayoutVisibility.GONE);
    }
    
    public void onBtnCloseTab(View v) {
    	this.webController.closeTab(this.webController.currentId());
    	
    	if (this.webController.currentId() == -1) {
    		finish();
    	} else {
    		((ArrayAdapter)tabList.getAdapter()).notifyDataSetChanged();
    	}
    }
    
    public void onBtnShare(View v) {
    	Intent intent = new Intent(android.content.Intent.ACTION_SEND);
    	intent.setType("text/plain");
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    	intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
    	intent.putExtra(Intent.EXTRA_TEXT, webController.currentUrl());
    	startActivity(Intent.createChooser(intent, "Sharing URL"));
    }
    
    public void onBtnAddBookmark(View v) {
    	
    }

    public void onBtnHome(View v) {
    	webController.goTo(WebController.HOME);
    }
    
    
    private void toggleTabLayout(LayoutVisibility visibility) {
    	
    	if (visibility == LayoutVisibility.VISIBLE && !tabLayoutVisible) {
    		tabLayout.setVisibility(View.VISIBLE);
    		tabLayoutVisible = true;
    	} else if (visibility == LayoutVisibility.GONE && tabLayoutVisible) {
    		tabLayout.setVisibility(View.GONE);
    		tabLayoutVisible = false;
    	} else {
    		// Toggle
    		tabLayout.setVisibility((tabLayoutVisible) ? View.GONE : View.VISIBLE);
    		tabLayoutVisible = !tabLayoutVisible;
    	}
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

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.events.UrlModificationAware#pageReceived(java.lang.String, java.lang.String)
	 */
	@Override
	public void pageReceived(String url, String title) {
		historyHelper.add(this.historyDatabase, title, url);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		historyDatabase.close();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		historyDatabase = this.historyHelper.getWritableDatabase();
	}
	
}
