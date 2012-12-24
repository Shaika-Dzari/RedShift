package ca.n4dev.redshift;

import java.util.Date;

import ca.n4dev.redshift.bookmark.BookmarkDbHelper.Sort;
import ca.n4dev.redshift.events.OnListClickAware;
import ca.n4dev.redshift.history.HistoryAdapter;
import ca.n4dev.redshift.history.HistoryDbHelper;
import ca.n4dev.redshift.utils.PeriodUtils;
import ca.n4dev.redshift.utils.PeriodUtils.Period;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class HistoryActivity extends Activity implements SearchView.OnQueryTextListener, OnListClickAware {
	
	private HistoryDbHelper historyHelper;
	private SQLiteDatabase historyDatabase;
	private ListView lv;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        historyHelper = new HistoryDbHelper(this);
        historyDatabase = historyHelper.getReadableDatabase();
        
        // Load List
        lv = (ListView) findViewById(R.id.lstHistory);
        
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,	long id) {
				
				Cursor citem = (Cursor) lv.getItemAtPosition(position);
				int idx = citem.getColumnIndex(HistoryDbHelper.HISTORY_URL);
				String url = citem.getString(idx);
				Intent intent = new Intent();  
		    	intent.putExtra("url", url);
		    	setResult(RESULT_OK, intent);
		    	finish();
			}
		
        });
        
        Cursor c = historyHelper.query(historyDatabase, Period.YESTERDAY, Sort.BYDATE);
        lv.setAdapter(new HistoryAdapter(this, c, this));
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_history, menu);
        
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_hist_search).getActionView();
        searchView.setOnQueryTextListener(this);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Cursor c;
    	
        switch (item.getItemId()) {
            case android.R.id.home:
            	// app icon in action bar clicked; go home
                Intent intent = new Intent(this, BrowserActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
           
            case R.id.menu_clearhistory:
            	historyHelper.clear(historyDatabase);
            	c = historyHelper.query(historyDatabase, Period.INFINITY, Sort.BYDATE);
            	((HistoryAdapter)lv.getAdapter()).changeCursor(c);
            	
            case R.id.menu_history_last7days:
            	c = historyHelper.query(historyDatabase, Period.LAST7DAYS, Sort.BYDATE);
            	((HistoryAdapter)lv.getAdapter()).changeCursor(c);
            
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    

	@Override
	public void onPause() {
		super.onPause();
		historyDatabase.close();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		historyDatabase = this.historyHelper.getReadableDatabase();
	}

	/* (non-Javadoc)
	 * @see android.widget.SearchView.OnQueryTextListener#onQueryTextChange(java.lang.String)
	 */
	@Override
	public boolean onQueryTextChange(String newText) {
		filterSearchResult(newText);
		return false;
	}

	/* (non-Javadoc)
	 * @see android.widget.SearchView.OnQueryTextListener#onQueryTextSubmit(java.lang.String)
	 */
	@Override
	public boolean onQueryTextSubmit(String query) {
		filterSearchResult(query);
		return false;
	}
	
	private void filterSearchResult(String query) {
		
		Cursor c = this.historyHelper.search(this.historyDatabase, query, Period.LAST7DAYS);
		
		((HistoryAdapter)lv.getAdapter()).changeCursor(c);
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.events.OnListClickAware#onListClickEvent(android.view.View)
	 */
	@Override
	public void onListClickEvent(View v) {
		String id = v.getTag().toString();
		this.historyHelper.delete(historyDatabase, Long.parseLong(id));
		Cursor c = historyHelper.query(historyDatabase, Period.YESTERDAY, Sort.BYDATE);
		((HistoryAdapter)lv.getAdapter()).changeCursor(c);
	}
}
