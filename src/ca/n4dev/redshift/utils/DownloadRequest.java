/*
 * downloadService.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-12-26
 */ 
package ca.n4dev.redshift.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.URLUtil;

public class DownloadRequest extends Thread {
	
	private DownloadManager downloadManager;
	private Activity parent;
	private String requestUrl;
	private long requestId;
	
	public DownloadRequest(Activity parent, String requestUrl) {
		this.parent = parent;
		this.requestUrl = requestUrl;
	}
	
	public void run() {
		
		if (downloadManager == null) {
			downloadManager = (DownloadManager) this.parent.getSystemService(Context.DOWNLOAD_SERVICE);
		}
		
		DownloadManager.Request r = new Request(Uri.parse(requestUrl));
		
		r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		r.setAllowedOverRoaming(false);
		r.setTitle(requestUrl);
		r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(requestUrl, null, null));
		
		requestId = downloadManager.enqueue(r);
	}
	
	/**
	 * @return the requestId
	 */
	public long getRequestId() {
		return requestId;
	}
}
