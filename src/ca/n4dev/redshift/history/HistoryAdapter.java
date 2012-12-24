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
import ca.n4dev.redshift.events.OnListClickAware;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class HistoryAdapter extends CursorAdapter {

	private static final String TAG = "HistoryAdapter";
	
	private int titleIdx = -1;
	private int urlIdx;
	private int dateIdx;
	private int idIdx;
	private OnListClickAware onListClickAware;
	
	/**
	 * @param context
	 * @param textViewResourceId
	 */
	public HistoryAdapter(Context context, Cursor c, OnListClickAware onListClickAware) {
		super(context, c);
		this.onListClickAware = onListClickAware;
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
			idIdx = cursor.getColumnIndex(HistoryDbHelper.HISTORY_ID);
		}
		String url = cursor.getString(urlIdx);
		int urlSize = url.length();
		
		if (urlSize > 30) 
			url = url.substring(0, 25) + "..." + url.substring(urlSize - 5, urlSize);
		
		
		((TextView)view.findViewById(R.id.li_txt_hist_title)).setText(cursor.getString(titleIdx));
		((TextView)view.findViewById(R.id.li_txt_hist_date)).setText(cursor.getString(dateIdx));
		((TextView)view.findViewById(R.id.li_txt_hist_url)).setText(url);
		
		ImageButton btn = (ImageButton) view.findViewById(R.id.btn_hist_delete);
		btn.setTag(cursor.getInt(idIdx));
		
		
	}

	/* (non-Javadoc)
	 * @see android.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.list_item_history, parent, false);
		
		ImageButton button = (ImageButton) v.findViewById(R.id.btn_hist_delete);
		button.setFocusable(false);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onListClickAware.onListClickEvent(v);
			}
		});
		
		v.setBackgroundResource(R.drawable.redshift_tabmenu_item_dark);
		
		return v;
	}

}
