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
package ca.n4dev.redshift.adapter;

import java.util.List;

import ca.n4dev.redshift.R;
import ca.n4dev.redshift.controller.container.RsWebView;
import ca.n4dev.redshift.events.OnListClickAware;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class TabListAdapter extends ArrayAdapter<RsWebView> {
	
	private static class TabViewHolder {
		TextView url;
		ImageButton button;
	}
	
	private OnListClickAware onListClickAware;
	
	/**
	 * @param context
	 * @param textViewResourceId
	 */
	public TabListAdapter(Context context, List<RsWebView> listTab, OnListClickAware onListClickAware) {
		super(context, R.layout.list_item_tab, listTab);
		this.onListClickAware = onListClickAware;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.list_item_tab, parent, false);
			convertView.setBackgroundResource(R.drawable.redshift_menuurl_background_dark);
			
			// Setup Holder
			TabViewHolder holder = new TabViewHolder();
			holder.url = (TextView) convertView.findViewById(R.id.li_txt_tab_url);
			holder.button = (ImageButton) convertView.findViewById(R.id.btn_tab_close);
			convertView.setTag(holder);
			
			// Close button
			
			holder.button.setFocusable(false);
			holder.button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onListClickAware.onListClickEvent(v);
				}
			});
		}
		
		try {
			
			RsWebView r = this.getItem(position);
			String title = r.getTitle();
			int titleSize = title.length();
			
			if (titleSize > 30)
				title = title.substring(0, 25) + "..." + title.substring(titleSize - 5, titleSize);
			
			((TabViewHolder)convertView.getTag()).url.setText(title);
			((TabViewHolder)convertView.getTag()).button.setTag(r.getTabId());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return convertView;
	}

}
