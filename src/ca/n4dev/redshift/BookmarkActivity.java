package ca.n4dev.redshift;

import ca.n4dev.redshift.bookmark.AddBookmarkDialog;
import ca.n4dev.redshift.bookmark.BookmarkDbHelper;
import ca.n4dev.redshift.bookmark.BookmarksList;
import ca.n4dev.redshift.bookmark.BookmarkDbHelper.Sort;
import ca.n4dev.redshift.events.OnListClickAware;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

public class BookmarkActivity extends Activity implements OnListClickAware, SearchView.OnQueryTextListener {

	private BookmarksList bookmarkList;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        bookmarkList = new BookmarksList();
	    bookmarkList.setListclickAware(this);
        
        getFragmentManager().
	        beginTransaction().
	        add(R.id.bookmark_layout, bookmarkList).
	        commit();
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_bookmark, menu);
        
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(this);
        
        return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	// app icon in action bar clicked; go home
                Intent intent = new Intent(this, BrowserActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
                
            case R.id.menu_sort_bydate:
            	this.bookmarkList.sortData(Sort.BYDATE);
            	return true;
            	
            case R.id.menu_sort_bytitle:
            	this.bookmarkList.sortData(Sort.BYTITLE);
            	return true;
            
            case R.id.menu_add_bookmark:
            	AddBookmarkDialog dialog = new AddBookmarkDialog();
        		dialog.show(getFragmentManager(), "AddBookmarkDialog");
            	return true;
            	
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.events.OnListClickAware#onListClickEvent(android.view.View)
	 */
	@Override
	public void onListClickEvent(View v) {
		TextView t = (TextView) v.findViewById(R.id.li_txt_url);
		String url = t.getText().toString();
		Intent intent=new Intent();  
    	intent.putExtra("url", url);
    	setResult(RESULT_OK, intent);
    	finish();
	}

	/* (non-Javadoc)
	 * @see android.widget.SearchView.OnQueryTextListener#onQueryTextChange(java.lang.String)
	 */
	@Override
	public boolean onQueryTextChange(String newText) {
		this.bookmarkList.filterSearchResult(newText);
		return false;
	}

	/* (non-Javadoc)
	 * @see android.widget.SearchView.OnQueryTextListener#onQueryTextSubmit(java.lang.String)
	 */
	@Override
	public boolean onQueryTextSubmit(String query) {
		this.bookmarkList.filterSearchResult(query);
		return false;
	}
}
