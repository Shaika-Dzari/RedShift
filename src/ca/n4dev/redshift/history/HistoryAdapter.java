/*
 * HistoryAdapter.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-12-16
 */ 
package ca.n4dev.redshift.history;

import ca.n4dev.redshift.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class HistoryAdapter extends CursorAdapter {

	private int titleIdx = -1;
	private int urlIdx;
	private int dateIdx;
	
	/**
	 * @param context
	 * @param textViewResourceId
	 */
	public HistoryAdapter(Context context, Cursor c) {
		super(context, c);
	}
	

	/* (non-Javadoc)
	 * @see android.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		if (titleIdx == -1) {
			titleIdx = cursor.getColumnIndex(HistoryDbHelper.HISTORY_TITLE);
			urlIdx = cursor.getColumnIndex(HistoryDbHelper.HISTORY_URL);
			dateIdx = cursor.getColumnIndex(HistoryDbHelper.HISTORY_PRETTYDATE);
		}
		
		((TextView)view.findViewById(R.id.li_txt_hist_title)).setText(cursor.getString(titleIdx));
		((TextView)view.findViewById(R.id.li_txt_hist_url)).setText(cursor.getString(urlIdx));
		((TextView)view.findViewById(R.id.li_txt_hist_date)).setText(cursor.getString(dateIdx));
	}

	/* (non-Javadoc)
	 * @see android.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.list_item_history, parent, false);
	
		return v;
	}

}
