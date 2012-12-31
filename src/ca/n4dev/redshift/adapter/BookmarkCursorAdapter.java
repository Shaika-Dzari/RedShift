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

	private boolean noIdx = true;
	private int titleIdx;
	private int urlIdx;
	private int dateIdx;
	
	private static class BookmarkHolder {
		TextView bTitle;
		TextView bUrl;
		TextView bDate;
		TextView bTags;
	}
	
	public BookmarkCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		BookmarkHolder holder = (BookmarkHolder) view.getTag();
		if (noIdx) {
			noIdx = false;
			titleIdx = cursor.getColumnIndex(BookmarkDbHelper.BOOKMARK_TITLE);
			urlIdx = cursor.getColumnIndex(BookmarkDbHelper.BOOKMARK_URL);
			dateIdx = cursor.getColumnIndex(BookmarkDbHelper.BOOKMARK_PRETTYDATE);
		}
		
		holder.bTitle.setText(cursor.getString(titleIdx));
		holder.bUrl.setText(cursor.getString(urlIdx));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.list_item_bookmark, parent, false);
		
		BookmarkHolder holder = new BookmarkHolder();
		holder.bTitle = (TextView)v.findViewById(R.id.li_txt_title);
		holder.bUrl = (TextView)v.findViewById(R.id.li_txt_url);
		
		v.setTag(holder);
		
		v.setBackgroundResource(R.drawable.redshift_tabmenu_item_dark);
		
		return v;
	}

}
