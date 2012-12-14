/**
 * 
 */
package ca.n4dev.redshift.bookmark;

import ca.n4dev.redshift.R;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * @author rguillemette
 *
 */
public class BookmarkCursorAdapter extends CursorAdapter {

	public BookmarkCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.list_item_bookmark, parent, false);
		
		return null;
	}

}
