/*
 * BrowserUi.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2013-01-01
 */ 
package ca.n4dev.redshift;

import java.lang.ref.WeakReference;

import ca.n4dev.redshift.R;
import ca.n4dev.redshift.controller.RsWebViewController;
import ca.n4dev.redshift.controller.api.TooManyTabException;
import ca.n4dev.redshift.controller.api.WebController;
import ca.n4dev.redshift.controller.container.RsWebView;
import ca.n4dev.redshift.events.ProgressAware;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView.HitTestResult;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

public class BrowserUi implements ProgressAware {
	
	private static final int BOOKMARK_RESULT_ID = 9990;
	private static final int HISTORY_RESULT_ID = 9991;
	
	private static final int REQUEST_NEW_TAB = 101;
	private static final int REQUEST_NEW_PRIVATE_TAB = 102;
	
	private static final String TOOMANYTAB = "Too many tab";
	
	private BrowserActivity parent;
	private ProgressBar progressBar = null;
	private PopupMenu popupMenu = null;
	private WebController controller;
	
	private class MyHandler extends Handler {
		private WeakReference<WebController> reference;
		
		public MyHandler(WebController web) {
			this.reference = new WeakReference<WebController>(web);
		}
		
		@Override
	    public void handleMessage(Message msg) {
			super.handleMessage(msg);
            String url = (String) msg.getData().get("url");
            
            boolean privateBrowing = msg.what == REQUEST_NEW_PRIVATE_TAB;
            
			try {
				int id = this.reference.get().newTab(parent, privateBrowing);
				this.reference.get().setCurrentTab(id);
				this.reference.get().goTo(url, true);
				//parent.urlHasChanged(url);
				
			} catch (TooManyTabException e) {
				showToastMessage(TOOMANYTAB);
			}
		}
	}
	
	private MyHandler handler;
	
	public BrowserUi(BrowserActivity parent, WebController controller) {
		this.parent = parent;
		this.controller = controller;
		((RsWebViewController)this.controller).setProgressAware(this);
		handler = new MyHandler(controller);
		
		progressBar = (ProgressBar)parent.findViewById(R.id.browser_progressbar);
	}
	
	public void showPopupmenu(View v) {
		
		if (popupMenu == null) {
			popupMenu = new PopupMenu(parent, v);
			popupMenu.getMenuInflater().inflate(R.menu.activity_browser, popupMenu.getMenu());
			
			popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					Intent intent;
					
					switch (item.getItemId()) {
					
					case R.id.menu_settings:
						intent = new Intent(parent, SettingsActivity.class);
						parent.startActivity(intent);
						break;
					case R.id.menu_bookmark:
						intent = new Intent(parent, BookmarkActivity.class);
						parent.startActivityForResult(intent, BOOKMARK_RESULT_ID);
						break;
						
					case R.id.menu_history:
						intent = new Intent(parent, HistoryActivity.class);
						parent.startActivityForResult(intent, HISTORY_RESULT_ID);
						break;
						
					case R.id.menu_quit:
						parent.cleanAndFinish();
						break;
						
					}
					
					return true;
				}
			});
			
		}
        
		popupMenu.show();
	}
	
	public void createContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		// Inflate menu
		parent.getMenuInflater().inflate(R.menu.menu_webviewcontext, menu);
		
		HitTestResult result = ((RsWebView)v).getHitTestResult();
		int type = result.getType();
		final String extra = result.getExtra();
		menu.setHeaderTitle(extra);
	
		// Show section
		
		menu.setGroupVisible(R.id.menu_web_link, type == HitTestResult.SRC_ANCHOR_TYPE || type == HitTestResult.SRC_IMAGE_ANCHOR_TYPE);
		menu.setGroupVisible(R.id.menu_web_image, type == HitTestResult.IMAGE_TYPE || type == HitTestResult.SRC_IMAGE_ANCHOR_TYPE);
		
		
		if (type == HitTestResult.SRC_ANCHOR_TYPE) {
			menu.findItem(R.id.menu_wl_newtab).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					try {
						int id = controller.newTab(parent, false);
						controller.setCurrentTab(id);
						controller.goTo(extra, false);
					} catch (TooManyTabException e) {
						showToastMessage(TOOMANYTAB);
					}
					return true;
				}
			});
			
			menu.findItem(R.id.menu_wl_newprivtab).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					try {
						int id = controller.newTab(parent, true);
						controller.setCurrentTab(id);
						controller.goTo(extra, false);
					} catch (TooManyTabException e) {
						showToastMessage(TOOMANYTAB);
					}
					return true;
				}
			});
			
		} else if (type == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
			menu.findItem(R.id.menu_wl_newtab).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					Message msg = handler.obtainMessage(REQUEST_NEW_TAB);
					msg.setTarget(handler);
					controller.handleMessage(msg);
					return true;
				}
			});
			
			menu.findItem(R.id.menu_wl_newprivtab).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					Message msg = handler.obtainMessage(REQUEST_NEW_PRIVATE_TAB);
					msg.setTarget(handler);
					controller.handleMessage(msg);
					return true;
				}
			});
		}
		
	}
	
	public void showToastMessage(String msg) {
    	int duration = Toast.LENGTH_LONG;
    	Toast t = Toast.makeText(parent, msg, duration);
    	t.show();
    }

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
}
