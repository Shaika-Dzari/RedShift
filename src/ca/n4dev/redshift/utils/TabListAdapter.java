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

import ca.n4dev.redshift.R;
import ca.n4dev.redshift.controller.container.RsWebView;
import ca.n4dev.redshift.events.OnListClickAware;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class TabListAdapter extends ArrayAdapter<RsWebView> {
	
	private static class TabViewHolder {
		ImageView imgssl;
		TextView url;
		ImageButton button;
	}
	
	private TabViewHolder holder;
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
			convertView.setBackgroundResource(R.drawable.redshift_tabmenu_item_dark);
			
			// Setup Holder
			holder = new TabViewHolder();
			holder.url = (TextView) convertView.findViewById(R.id.li_txt_tab_url);
			holder.button = (ImageButton) convertView.findViewById(R.id.btn_tab_close);
			holder.imgssl = (ImageView) convertView.findViewById(R.id.li_txt_tab_ssl);
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
			String url = r.getUrl();
			int urlSize = url.length();
			
			if (urlSize > 30) 
				url = url.substring(0, 25) + "..." + url.substring(urlSize - 5, urlSize);
			
			
			((TabViewHolder)convertView.getTag()).url.setText(url);
			((TabViewHolder)convertView.getTag()).button.setTag(r.getTabId());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return convertView;
	}

}
