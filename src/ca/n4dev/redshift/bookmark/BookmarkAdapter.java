/**
 * 
 */
package ca.n4dev.redshift.bookmark;

import java.util.List;

import ca.n4dev.redshift.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * @author rguillemette
 *
 */
public class BookmarkAdapter extends ArrayAdapter<Bookmark> {

	public BookmarkAdapter(Context context, List<Bookmark> bookmarks) {
		super(context, R.layout.list_item_bookmark, bookmarks);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.list_item_bookmark, parent, false);
			convertView.setBackgroundResource(R.drawable.redshift_tabmenu_item_dark);
		}
		
		Bookmark b = getItem(position);
		TextView title = (TextView) convertView.findViewById(R.id.li_txt_title);
		TextView url = (TextView) convertView.findViewById(R.id.li_txt_url);
		
		title.setText(b.title);
		url.setText(b.url);
		
		
		
		return convertView;
	}
}
