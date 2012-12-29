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
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class AddBookmarkDialog extends DialogFragment {
	
	private String title = null;
	private String url = null;
	private Long bookmarkId = null;
	private String tags = null;
	private boolean showInhome = false;
	private View view;
	
	private BookmarkDbHelper dbHelper;
	private SQLiteDatabase db;
	
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		dbHelper = new BookmarkDbHelper(getActivity());
		db = dbHelper.getWritableDatabase();
		
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
        	EditText txtUrl = (EditText) view.findViewById(R.id.txt_ab_url);
        	txtUrl.setText(url);
        }
        
        if (tags != null) {
        	EditText txtTags = (EditText) view.findViewById(R.id.txt_ab_tag);
        	txtTags.setText(tags);
        }
        
        if (showInhome != false) {
        	CheckBox sih = (CheckBox) view.findViewById(R.id.ck_ab_showinhome);
        	sih.setChecked(true);
        }
        
        
        builder.setView(view);
        
      
        
        //button
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            	
                String t = ((EditText) view.findViewById(R.id.txt_ab_title)).getText().toString();
            	String u = ((EditText) view.findViewById(R.id.txt_ab_url)).getText().toString();
            	String a = ((EditText) view.findViewById(R.id.txt_ab_tag)).getText().toString();
            	boolean c = ((CheckBox)view.findViewById(R.id.ck_ab_showinhome)).isChecked();
            	String sih = (c) ? "1" : "0";
            	
            	if (bookmarkId == null)
            		dbHelper.add(dbHelper.getWritableDatabase(), t, u, a, sih); // insert
            	else {
            		ContentValues values = new ContentValues();
            		values.put(BookmarkDbHelper.BOOKMARK_TITLE, t);
            		values.put(BookmarkDbHelper.BOOKMARK_TAG, a);
            		values.put(BookmarkDbHelper.BOOKMARK_URL, u);
            		values.put(BookmarkDbHelper.BOOKMARK_SHOWINHOME, sih);
            		
            		db.update(BookmarkDbHelper.BOOKMARK_TABLE_NAME, values, BookmarkDbHelper.BOOKMARK_ID + " = ?", new String[]{"" + bookmarkId});
            	}
            	close();
            }
        })
        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	close();
            	AddBookmarkDialog.this.getDialog().cancel();
            }
        });
        
        return builder.create();
	}

	
	
	public void close() {
		db.close();
		db = null;
		dbHelper = null;
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


	/**
	 * @return the id
	 */
	public Long getBookmarkId() {
		return bookmarkId;
	}


	/**
	 * @param id the id to set
	 */
	public void setBookmarkId(Long bookmarkId) {
		this.bookmarkId = bookmarkId;
	}


	/**
	 * @return the tags
	 */
	public String getTags() {
		return tags;
	}


	/**
	 * @param tags the tags to set
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}



	/**
	 * @return the showInhome
	 */
	public boolean isShowInhome() {
		return showInhome;
	}



	/**
	 * @param showInhome the showInhome to set
	 */
	public void setShowInhome(boolean showInhome) {
		this.showInhome = showInhome;
	}
}
