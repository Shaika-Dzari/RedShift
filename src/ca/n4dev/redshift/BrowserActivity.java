package ca.n4dev.redshift;


import ca.n4dev.redshift.R;
import ca.n4dev.redshift.adapter.TabListAdapter;
import ca.n4dev.redshift.controller.RsWebViewController;
import ca.n4dev.redshift.controller.api.TooManyTabException;
import ca.n4dev.redshift.controller.container.RsWebView;
import ca.n4dev.redshift.events.OnListClickAware;
import ca.n4dev.redshift.events.UrlModificationAware;
import ca.n4dev.redshift.fragment.AddBookmarkDialog;
import ca.n4dev.redshift.persistence.HistoryDbHelper;
import ca.n4dev.redshift.persistence.SettingsKeys;
import ca.n4dev.redshift.utils.UrlUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;


@SuppressLint("SetJavaScriptEnabled")
public class BrowserActivity extends FragmentActivity implements UrlModificationAware,
																	 OnListClickAware {
	
	private static final String TAG = "BrowserActivity";
	private static final int BOOKMARK_RESULT_ID = 9990;
	private static final int HISTORY_RESULT_ID = 9991;
	public static final boolean LOG_ENABLE = true;
	
	private RsWebViewController webController;
	private HistoryDbHelper historyHelper;
	private ListView tabList = null;
	private LinearLayout tabLayout = null;
	private SharedPreferences preferences;
	
	private Handler runnableHandler;
	private Runnable hideMenu = new Runnable() {
		@Override
		public void run() {
			toggleTabLayout(LayoutVisibility.GONE);
		}
	};
	
	
	// Some preferences
	private boolean prefHistory;
	
	private boolean tabLayoutVisible = false;
	
	private enum LayoutVisibility {
		VISIBLE, GONE, TOGGLE
	}
	
	private BrowserUi ui;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        
        setContentView(R.layout.activity_browser);
        this.webController = new RsWebViewController(this, (FrameLayout) findViewById(R.id.layout_content), this);
        this.ui = new BrowserUi(this, this.webController);
                
        // No ActionBar in browser
        getActionBar().hide();
        
        runnableHandler = new Handler();
        
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        historyHelper = new HistoryDbHelper(this);
        historyHelper.openDb();
        	
        String initUrl = null;
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        
        
        // Are we coming from another app with intent ?
        if (data != null && action.equalsIgnoreCase("android.intent.action.VIEW")) {
        	initUrl = data.toString();
        } 
        
        
        
        // Set submit event on edittext
        EditText txt = (EditText) findViewById(R.id.txtUrl);
        txt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO) {
					clearFocus(v);					
					String url = v.getText().toString();
					url = UrlUtils.sanitize(url);
					webController.goTo(url, false);
					return true;
				}
				return false;
			}
		});
        
    	if (savedInstanceState == null) {
    		int homeTab;
			try {
				homeTab = this.webController.newTab(this, false);
				this.webController.setCurrentTab(homeTab);  
				
				
				if (initUrl == null) {
					this.webController.goToHome();
				} else if (initUrl.equalsIgnoreCase("redshift:about")) {
					urlHasChanged("redshift:about");
					this.webController.goTo("file:///android_asset/about.html", false);
				} else {
					this.webController.goTo(initUrl, true);
				} 
				
				/*
				if (initUrl != null) {
					
					//urlHasChanged(initUrl);
					this.webController.goTo(initUrl, true);
				}
				else
					this.webController.goToHome();
				*/
				
			} catch (TooManyTabException e) {
				ui.showToastMessage("Too Many Tabs");
			}
    	} else {
    		Log.d(TAG, "onCreate#restoreState");
        	this.webController.restoreState(savedInstanceState);
        	this.webController.setCurrentTab(this.webController.currentId());
    	}
        
    }
    
    private void clearFocus(View v) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
    
    @Override
    protected void onSaveInstanceState (Bundle outState) {
    	super.onSaveInstanceState(outState);
    	Log.d(TAG, "onSaveInstanceState");
    	this.webController.saveState(outState);
    	
    }
    
    protected void onRestoreInstanceState (Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	Log.d(TAG, "onRestoreInstanceState");
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	String url = null;
    	if (data != null) {
    		if (requestCode == BOOKMARK_RESULT_ID || requestCode == HISTORY_RESULT_ID) {
    			url = data.getStringExtra("url");
    		
	    		if (url != null && !"".equals(url)) {
	    			//urlHasChanged(url);
	    			webController.goTo(url, true);
	    		}
    		}
    	}
    }
    
    public void onBtnShowPopup(View v) {
    	ui.showPopupmenu(v);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	return super.onOptionsItemSelected(item);
    }
    
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	ui.createContextMenu(menu, v, menuInfo);
    }
    
    
    /* Activity Events */
    public void onBtnListTab(View v) {
    	
    	if (tabLayoutVisible == true) {
    		toggleTabLayout(LayoutVisibility.GONE);
    		
    	} else {
    	
	    	if (tabList == null) {
	    		
	    		tabList = (ListView) findViewById(R.id.list_tab);
	    		tabLayout = (LinearLayout) findViewById(R.id.layout_tabmenu);
	    		
	    		tabList.setAdapter(new TabListAdapter(this, this.webController.listTab(), this));
	    		tabList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	
					@Override
					public void onItemClick(AdapterView<?> arg0, View view, int position,	long id) {
						RsWebView r = (RsWebView) tabList.getItemAtPosition(position);
						urlHasChanged(r.getUrl());
						webController.setCurrentTab(r.getTabId());
						toggleTabLayout(LayoutVisibility.GONE);
					}
	    			
	    		});
	    		
	    	}
    	
	    	((ArrayAdapter)tabList.getAdapter()).notifyDataSetChanged();
	    	toggleTabLayout(LayoutVisibility.VISIBLE);
    	}
    }
    
    
    
    public void onBtnNewTab(View v) {
    	toggleTabLayout(LayoutVisibility.GONE);
    	int newTab;
		try {
			newTab = this.webController.newTab(this, false);
			this.webController.setCurrentTab(newTab);
			this.webController.goToHome();
			((ArrayAdapter)tabList.getAdapter()).notifyDataSetChanged();
			
		} catch (TooManyTabException e) {
			ui.showToastMessage("Too many tab.");
		}
    }
    
    public void onBtnNewPrivateTab(View v) {
    	toggleTabLayout(LayoutVisibility.GONE);
    	int newTab;
		try {
			newTab = this.webController.newTab(this, true);
			this.webController.setCurrentTab(newTab);
			this.webController.goToHome();
			((ArrayAdapter)tabList.getAdapter()).notifyDataSetChanged();
			
		} catch (TooManyTabException e) {
			ui.showToastMessage("Too many tab.");
		}
    }
    
    public void onBtnShare(View v) {
    	toggleTabLayout(LayoutVisibility.GONE);
    	Intent intent = new Intent(android.content.Intent.ACTION_SEND);
    	intent.setType("text/plain");
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    	intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
    	intent.putExtra(Intent.EXTRA_TEXT, webController.currentUrl());
    	startActivity(Intent.createChooser(intent, "Sharing URL"));
    }
    
    public void onBtnAddBookmark(View v) {
    	toggleTabLayout(LayoutVisibility.GONE);
    	AddBookmarkDialog dialog = new AddBookmarkDialog();
    	
    	dialog.setTitle(this.webController.currentTitle());
    	dialog.setUrl(this.webController.currentUrl());
    	
    	dialog.show(getFragmentManager(), "AddBookmarkDialog");
    }

    public void onBtnHome(View v) {
    	toggleTabLayout(LayoutVisibility.GONE);
    	webController.goToHome();
    }
    
    private void toggleTabLayout(LayoutVisibility visibility) {
    	
    	if (visibility == LayoutVisibility.VISIBLE && !tabLayoutVisible) {
    		runnableHandler.postDelayed(hideMenu, 5000);
    		tabLayout.setVisibility(View.VISIBLE);
    		tabLayoutVisible = true;
    	} else if (visibility == LayoutVisibility.GONE && tabLayoutVisible) {
    		runnableHandler.removeCallbacks(hideMenu);
    		tabLayout.setVisibility(View.GONE);
    		tabLayoutVisible = false;
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
	 * @see ca.n4dev.redshift.events.UrlModificationAware#pageReceived(java.lang.String, java.lang.String)
	 */
	@Override
	public void pageReceived(String url, String title) {
		if (prefHistory && url.indexOf("about:") == -1 && url.indexOf("redshift:") == -1 && !this.webController.isCurrentTabPrivate()) {
			
			final String t = title; 
			final String u = url;
			
			runnableHandler.post(new Runnable() {
				@Override
				public void run() {
					historyHelper.add(t, u);
				}
			});
			
		}
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		this.historyHelper.closeDb();
		this.webController.pause();
	}
	
	@Override
	public void onNewIntent(Intent intent) {
		Log.d(TAG, "onnewIntent");
		String exd = intent.getStringExtra("url");
        
        // Are we coming from another app with intent ?
        if (exd != null && exd.equals("redshift:about")) {
        	
        	try {
        		
				int t = this.webController.newTab(this, false);
				this.webController.setCurrentTab(t);
				this.webController.goTo("file:///android_asset/about.html", false);
				urlHasChanged("redshift:about");
			} catch (TooManyTabException e) {
				e.printStackTrace();
			}
        	
        } 
	}
	
	@Override
	public void onResume() {
		super.onResume();
		this.historyHelper.openDb();
		prefHistory = preferences.getBoolean(SettingsKeys.KEY_HISTORY, true);
		
		this.webController.resume();	
	}


	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.events.OnListClickAware#onListClickEvent(android.view.View)
	 */
	@Override
	public void onListClickEvent(View v) {
		String id = v.getTag().toString();
		this.webController.closeTab(Integer.parseInt(id));
		
    	if (this.webController.currentId() == -1) {
    		cleanAndFinish();
    	} else {
    		((ArrayAdapter)tabList.getAdapter()).notifyDataSetChanged();
    	}
	}
	
	void cleanAndFinish() {
		this.webController.close();
		finish();
	}

}
