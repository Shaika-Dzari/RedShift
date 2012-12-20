/*
 * CookieDbHelper.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-12-19
 */ 
package ca.n4dev.redshift.controller.cookie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CookieDbHelper extends SQLiteOpenHelper {

	private static final int 	DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "webviewCookiesChromium.db";
	
	
	
	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public CookieDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		/*
		 
			        	CookieDbHelper cookieHelper = new CookieDbHelper(browserActivity);
			        	
			        	SQLiteDatabase db = cookieHelper.getWritableDatabase();
			        	int d = db.delete("cookies", "host_key like ?", new String[]{"%msn%"});
			        	db.close();
			        	
			        	Log.d("CCC", "===>   " + d); 
		 */
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
