package ca.n4dev.redshift;


import java.util.List;

import ca.n4dev.redshift.R;
import ca.n4dev.redshift.adapter.TabListAdapter;
import ca.n4dev.redshift.controller.RsWebViewController;
import ca.n4dev.redshift.controller.api.TooManyTabException;
import ca.n4dev.redshift.controller.container.RsWebView;
import ca.n4dev.redshift.events.OnListClickAware;
import ca.n4dev.redshift.events.ProgressAware;
import ca.n4dev.redshift.events.UrlModificationAware;
import ca.n4dev.redshift.events.WebViewOnMenuItemClickListener;
import ca.n4dev.redshift.fragment.AddBookmarkDialog;
import ca.n4dev.redshift.persistence.HistoryDbHelper;
import ca.n4dev.redshift.persistence.SettingsKeys;
import ca.n4dev.redshift.utils.DownloadRequest;
import ca.n4dev.redshift.utils.UrlUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView.HitTestResult;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;


@SuppressLint("SetJavaScriptEnabled")
public class BrowserActivity extends FragmentActivity implements UrlModificationAware, 
																	 ProgressAware, 
																	 OnListClickAware {
	
	private static final String TAG = "BrowserActivity";
	private static final int BOOKMARK_RESULT_ID = 9990;
	private static final int HISTORY_RESULT_ID = 9991;
	public static final boolean LOG_ENABLE = true;
	
	private RsWebViewController webController;
	private ProgressBar progressBar;
	private HistoryDbHelper historyHelper;
	private BrowserActivity browserActivity;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        browserActivity = this;
        setContentView(R.layout.activity_browser);
                
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
        
        // Get progress bar to provide loading feedback
        progressBar = (ProgressBar) findViewById(R.id.browser_progressbar);
        
        
        // Set submit event on edittext
        EditText txt = (EditText) findViewById(R.id.txtUrl);
        txt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO) {
					clearFocus(v);					
					String url = v.getText().toString();
					url = UrlUtils.sanitize(url);
					webController.goTo(url);
					return true;
				}
				return false;
			}
		});
        
        // create WebController
        if (this.webController == null) {
        	Log.d(TAG, "Creating WebController.");
        	//this.webController = new RsWebController(R.id.layout_content, getFragmentManager(), this, this);
        	this.webController = new RsWebViewController(this, (FrameLayout) findViewById(R.id.layout_content), this, this);
        	
        	if (savedInstanceState == null) {
        		int homeTab;
				try {
					homeTab = this.webController.newTab(this, false);
					this.webController.setCurrentTab(homeTab);  
					
					if (initUrl != null) {
						urlHasChanged(initUrl);
						this.webController.goTo(initUrl);
					}
					else
						this.webController.goToHome();
					
				} catch (TooManyTabException e) {
					showToastMessage("Too Many Tabs");
				}
        	} else {
        		Log.d(TAG, "onCreate#restoreState");
            	this.webController.restoreState(savedInstanceState);
            	this.webController.setCurrentTab(this.webController.currentId());
        	}
        }
    }
    
    private void clearFocus(View v) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
    
    private void showToastMessage(String msg) {
    	int duration = Toast.LENGTH_LONG;
    	Toast t = Toast.makeText(this, msg, duration);
    	t.show();
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
			        	cleanAndFinish();
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
    
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	HitTestResult result = ((RsWebView)v).getHitTestResult();
    	
    	if (result.getType() == HitTestResult.SRC_ANCHOR_TYPE) {
    		menu.setHeaderTitle(result.getExtra());
    		
    		menu.add(0, 0, 1, "Open new Tab")
    			.setOnMenuItemClickListener(
    					new WebViewOnMenuItemClickListener(this, this.webController, result.getExtra()));
    		
    		menu.add(0, 1, 2, "Open new Private Tab")
    			.setOnMenuItemClickListener(
    					new WebViewOnMenuItemClickListener(this, this.webController, result.getExtra(), true));
    	
    	} else if (result.getType() == HitTestResult.IMAGE_TYPE || result.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
    		final DownloadRequest downloadRequest = new DownloadRequest(browserActivity, result.getExtra());
    		menu.setHeaderTitle(result.getExtra());
    		menu.add(0, 0, 1, "Save image")
			.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					downloadRequest.start();
					return true;
				}
			});
		
    	}
    	
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
			showToastMessage("Too many tab.");
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
			showToastMessage("Too many tab.");
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
		if (prefHistory && url.indexOf("about:") == -1 && url.indexOf("redshift:") == -1 && !this.webController.isCurrentTabPrivate())
			historyHelper.add(title, url);
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause()");
		this.historyHelper.closeDb();
		this.webController.pause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume()");
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
	
	private void cleanAndFinish() {
		this.webController.close();
		finish();
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.events.UrlModificationAware#requestYoutubeOpening(java.lang.String)
	 */
	@Override
	public boolean requestYoutubeOpening(String videoId) {
		
		if (videoId != null) {
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId)); 
	    	List<ResolveInfo> list = getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY); 
	    	if (list.size() == 0)
	    		return false;
	   
	    	startActivity(i);
	    	return true;
		} else {
			Intent i = new Intent(Intent.ACTION_MAIN);
			i.setPackage("com.google.android.youtube");
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			try {	
			    startActivity(i);
			    return true;
			} catch (android.content.ActivityNotFoundException anfe) {
			    return false;
			}
		}
		
		
		/*
		String vnd = "vnd.youtube" + ((videoId != null) ? ":" + videoId : "");
		
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(vnd)); 
    	
    	List<ResolveInfo> list = getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY); 
    	if (list.size() == 0) { 
    		i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com")); 
    		list = getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
    		
    		return false;
    	}
    	
    	startActivity(i);
		return true;
		
		*/
		
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.events.UrlModificationAware#requestPlayOpening(java.lang.String)
	 */
	@Override
	public boolean requestPlayOpening(String packageName) {
		try {			
		    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
		    return false;
		} catch (android.content.ActivityNotFoundException anfe) {
		    return false;
		}
	}
	
}
