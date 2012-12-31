/*
 * BookmarksList.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-12-10
 */ 
package ca.n4dev.redshift.fragment;

import ca.n4dev.redshift.R;
import ca.n4dev.redshift.adapter.BookmarkCursorAdapter;
import ca.n4dev.redshift.events.OnListClickAware;
import ca.n4dev.redshift.persistence.BookmarkDbHelper;
import ca.n4dev.redshift.persistence.HistoryDbHelper;
import ca.n4dev.redshift.persistence.Sort;
import ca.n4dev.redshift.utils.PeriodUtils.Period;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

public class BookmarksList extends ListFragment {
	
	private static final String TAG = "BookmarksList";
	
	private BookmarkDbHelper dbHelper;

	private OnListClickAware onListClickAware;
	
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
                
        this.dbHelper = new BookmarkDbHelper(getActivity());
        this.dbHelper.openDb();
        Cursor c = dbHelper.queryAll();
        
        setListAdapter(new BookmarkCursorAdapter(getActivity(), c));
     
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    	MenuInflater inflater = getActivity().getMenuInflater();
    	inflater.inflate(R.menu.menu_bookmarklongclick, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return false;
        }
     
        switch (item.getItemId()) {
			case R.id.menu_bo_edit:
				Log.d(TAG, "menu_bo_edit");
				showEditBookmark( (Cursor)getListView().getItemAtPosition(info.position) );
				break;
	
			case R.id.menu_bo_delete:
				showDeleteBookmark((Cursor)getListView().getItemAtPosition(info.position));
				break;
		}
        
        
        return true;
    }
    
    private void showEditBookmark(Cursor citem) {
    	AddBookmarkDialog dialog = new AddBookmarkDialog();
		
		dialog.setBookmarkId(citem.getLong(citem.getColumnIndex(BookmarkDbHelper.BOOKMARK_ID)));
		dialog.setUrl(citem.getString(citem.getColumnIndex(BookmarkDbHelper.BOOKMARK_URL)));
		dialog.setTitle(citem.getString(citem.getColumnIndex(BookmarkDbHelper.BOOKMARK_TITLE)));
		dialog.setTags(citem.getString(citem.getColumnIndex(BookmarkDbHelper.BOOKMARK_TAG)));
		
		dialog.show(getFragmentManager(), "AddBookmarkDialog");
    }
    
    private boolean showDeleteBookmark(Cursor citem) {
    	
    	final Long bookmarkId = citem.getLong(citem.getColumnIndex(BookmarkDbHelper.BOOKMARK_ID));
    	String url = citem.getString(citem.getColumnIndex(BookmarkDbHelper.BOOKMARK_URL));
    	
    	if (url.length() > 25)
    		url = url.substring(0, 25) + "...";
    	
    	Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Delete " + url + "?");
		
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	              dbHelper.delete(bookmarkId);
	           }
	       });
		
		builder.setNegativeButton(android.R.string.cancel, null);
		
		AlertDialog dialog = builder.create();
		dialog.show();
		
    	return false;
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	this.onListClickAware.onListClickEvent(v);
    }
    
    public void setListclickAware(OnListClickAware onListClickAware) {
    	this.onListClickAware = onListClickAware;
    }
    
    public void filterSearchResult(String query) {
		Cursor c = this.dbHelper.search(query, Period.INFINITY);
		((BookmarkCursorAdapter)getListAdapter()).changeCursor(c);
	}
    
    public void sortData(Sort sort) {
    	Cursor c = dbHelper.queryAll(sort);
    	((BookmarkCursorAdapter)getListAdapter()).changeCursor(c);
    }
    
    @Override
	public void onResume(){
        super.onResume();
        this.dbHelper.openDb();
    }
    
    @Override
	public void onPause(){
        super.onPause();
        this.dbHelper.closeDb();
    }

}
