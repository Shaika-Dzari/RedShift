/*
 * TabListAdapter.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-12-20
 */ 
package ca.n4dev.redshift.utils;

import java.util.List;

import ca.n4dev.redshift.controller.container.RsWebView;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TabListAdapter extends ArrayAdapter<RsWebView> {
	
	/**
	 * @param context
	 * @param textViewResourceId
	 */
	public TabListAdapter(Context context, List<RsWebView> listTab) {
		super(context, android.R.layout.simple_list_item_1, listTab);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = ((Activity)getContext()).getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
		}
		
		TextView url = (TextView) convertView.findViewById(android.R.id.text1);
		RsWebView r = this.getItem(position);
		
		url.setText(r.getUrl());
		
		return convertView;
	}

}
