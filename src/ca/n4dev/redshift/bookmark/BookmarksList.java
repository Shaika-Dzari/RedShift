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
package ca.n4dev.redshift.bookmark;

import ca.n4dev.redshift.R;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class BookmarksList extends ListFragment {
	
	private boolean mDualPane;
	private int mCurCheckPosition = 0;
	private BookmarkDbHelper dbHelper = new BookmarkDbHelper(getActivity());
	private SQLiteDatabase db;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        db = dbHelper.getReadableDatabase();
        dbHelper.insertTestData(db);
        
        
        Cursor c = db.query(BookmarkDbHelper.BOOKMARK_TABLE_NAME,
        					BookmarkDbHelper.getBookmarkTableColumns(), 
        					null, 
        					null, 
        					null, 
        					null, 
        					BookmarkDbHelper.BOOKMARK_CREATIONDATE + " DESC");
        
        setListAdapter(new BookmarkCursorAdapter(getActivity(), c));
        
        View editFrame = getActivity().findViewById(R.id.frag_bookmarkedit);
        mDualPane = editFrame != null && editFrame.getVisibility() == View.VISIBLE;
        
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }
        
        
        if (mDualPane) {
            // In dual-pane mode, the list view highlights the selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // Make sure our UI is in the correct state.
            showDetails(mCurCheckPosition);
        }
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDetails(position);
    }
    
    void showDetails(int index) {
        mCurCheckPosition = index;

        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            getListView().setItemChecked(index, true);

            // Check what fragment is currently shown, replace if needed.
            BookmarkEditFragment details = (BookmarkEditFragment) getFragmentManager().findFragmentById(R.id.frag_bookmarkedit);
            if (details == null || details.getShownIndex() != index) {
                // Make new fragment to show this selection.
                details = BookmarkEditFragment.newInstance(index);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (index == 0) {
                    ft.replace(R.id.frag_bookmarkedit, details);
                /*
                } else {
                    ft.replace(R.id.a_item, details);
                    */
                }
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), BookmarkEditActivity.class);
            intent.putExtra("index", index);
            startActivity(intent);
        }
    }
    
    
    @Override
	public void onResume(){
        super.onResume();
        db = dbHelper.getReadableDatabase();
    }
    
    @Override
	public void onPause(){
        super.onPause();
        db.close();
    }

    @Override
	public void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
