/*
 * SpecialUrlSupport.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2013-01-06
 */ 
package ca.n4dev.redshift.utils;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.MailTo;
import android.net.Uri;

public class SpecialUrlSupport implements SpecialUrl {

	private Context context;
	
	public SpecialUrlSupport(Context context) {
		this.context = context;
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.utils.SpecialUrl#isOverride(java.lang.String)
	 */
	@Override
	public UrlType getUrlType(String url) {
		
		if (url.indexOf("http") == 0) {
			
			if (url.matches("^(http|https)://(www\\.)youtube\\.com.*?$"))
				return UrlType.YOUTUBE;
			if (url.matches("^(http|https)://play\\.google\\.com.*?$"))
				return UrlType.MARKET;
			
		} else {
			
			if (url.indexOf("market://") == 0)
				return UrlType.MARKET;
			if (url.indexOf("mailto:") == 0)
				return UrlType.MAILTO;
			if (url.indexOf("geo:") == 0)
				return UrlType.GEO;
			if (url.indexOf("tel:") == 0)
				return UrlType.DIAL;
		}
		
		
		
		return UrlType.HTTP;
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.utils.SpecialUrl#overrideUrlIntent(java.lang.String)
	 */
	@Override
	public void overrideUrlIntent(UrlType type, String url) {
		
		switch (type) {
		case YOUTUBE:
			Uri u = Uri.parse(url);
	    	String videoId = u.getQueryParameter("v");
	    	
	    	Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId)); 
	    	List<ResolveInfo> list = this.context.getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY); 
	    	if (list.size() != 0)
	    		this.context.startActivity(i);
	    	
			break;

		case MARKET:		
			this.context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			break;
		case DIAL:
			if (!url.contains("*") && !url.contains("#")) { // Prevent USSD command
				this.context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
			}
			break;
		case MAILTO:
		    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);  
		    MailTo mailto = MailTo.parse(url);
		    emailIntent.setType("plain/text"); 		    
		    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mailto.getTo()});
		    if (mailto.getCc() != null)
		    	emailIntent.putExtra(Intent.EXTRA_CC, mailto.getCc());
		    if (mailto.getBody() != null) 
		    	emailIntent.putExtra(Intent.EXTRA_TEXT, mailto.getBody());
		    if (mailto.getSubject() != null)
		    	emailIntent.putExtra(Intent.EXTRA_SUBJECT, mailto.getSubject());
		    
		    this.context.startActivity(Intent.createChooser(emailIntent, "Sending email"));
		    
			break;
			
		case GEO:
			
			break;
		}
		
	}
	
	
}
