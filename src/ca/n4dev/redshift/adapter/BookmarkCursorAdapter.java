/**
 * 
 */
package ca.n4dev.redshift.adapter;

import ca.n4dev.redshift.R;
import ca.n4dev.redshift.persistence.BookmarkDbHelper;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * @author rguillemette
 *
 */
public class BookmarkCursorAdapter extends CursorAdapter {

	private int titleIdx = -1;
	private int urlIdx;
	//private int tagIdx;
	private int dateIdx;
	
	public BookmarkCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		if (titleIdx == -1) {
			titleIdx = cursor.getColumnIndex(BookmarkDbHelper.BOOKMARK_TITLE);
			urlIdx = cursor.getColumnIndex(BookmarkDbHelper.BOOKMARK_URL);
			dateIdx = cursor.getColumnIndex(BookmarkDbHelper.BOOKMARK_PRETTYDATE);
		}
		
		((TextView)view.findViewById(R.id.li_txt_title)).setText(cursor.getString(titleIdx));
		((TextView)view.findViewById(R.id.li_txt_url)).setText(cursor.getString(urlIdx));
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.list_item_bookmark, parent, false);
		v.setBackgroundResource(R.drawable.redshift_tabmenu_item_dark);
		
		return v;
	}

}
