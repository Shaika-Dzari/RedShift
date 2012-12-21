/*
 * AddBookmarkDialog.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-12-20
 */ 
package ca.n4dev.redshift.bookmark;

import ca.n4dev.redshift.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class AddBookmarkDialog extends DialogFragment {
	
	private String title = null;
	private String url = null;
	private View view;
	
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        // Title
        //builder.setMessage(R.string.dia_addbookmark_title);
        builder.setTitle(R.string.dia_addbookmark_title);
        
        view = inflater.inflate(R.layout.dialog_add_bookmark, null);
        
        if (title != null) {
        	EditText txtTitle = (EditText) view.findViewById(R.id.txt_ab_title);
        	txtTitle.setText(title);
        }
        
        if (url != null) {
        	EditText urlTitle = (EditText) view.findViewById(R.id.txt_ab_url);
        	urlTitle.setText(url);
        }
        
        
        builder.setView(view);
        
      
        
        //button
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            	
                BookmarkDbHelper dbHelper = new BookmarkDbHelper(getActivity());
                String t = ((EditText) view.findViewById(R.id.txt_ab_title)).getText().toString();
            	String u = ((EditText) view.findViewById(R.id.txt_ab_url)).getText().toString();
            	String a = ((EditText) view.findViewById(R.id.txt_ab_tag)).getText().toString();
            	
            	dbHelper.add(dbHelper.getWritableDatabase(), t, u, a);
            }
        })
        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	AddBookmarkDialog.this.getDialog().cancel();
            }
        });
        
        return builder.create();
	}


	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}


	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}


	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}


	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
}
