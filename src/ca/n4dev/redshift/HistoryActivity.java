package ca.n4dev.redshift;

import java.util.Date;

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
import android.widget.TextView;

public class HistoryActivity extends Activity {
	
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
        
        lv = (ListView) findViewById(R.id.lstHistory);
        
        
        
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,	long id) {
				TextView t = (TextView) view.findViewById(R.id.li_txt_hist_url);
				String url = t.getText().toString();
				Intent intent = new Intent();  
		    	intent.putExtra("url", url);
		    	setResult(RESULT_OK, intent);
		    	finish();
			}
		
        });
        
        Cursor historyCursor = this.historyDatabase.query(
        					HistoryDbHelper.HISTORY_TABLE_NAME, 
        					HistoryDbHelper.getHistoryTableColumns(), 
        					HistoryDbHelper.HISTORY_CREATIONDATE + " >= ?", 
        					new String[] { PeriodUtils.getDateStringFrom(new Date(), Period.YESTERDAY) }, 
        					null, 
        					null, 
        					HistoryDbHelper.HISTORY_CREATIONDATE + " DESC");
        
        lv.setAdapter(new HistoryAdapter(this, historyCursor));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_history, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	// app icon in action bar clicked; go home
                Intent intent = new Intent(this, BrowserActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
           
            case R.id.menu_clearhistory:
            	historyHelper.clear(historyDatabase);
            	Cursor c = this.historyDatabase.query(
    					HistoryDbHelper.HISTORY_TABLE_NAME, 
    					HistoryDbHelper.getHistoryTableColumns(), 
    					HistoryDbHelper.HISTORY_CREATIONDATE + " >= ?", 
    					new String[] { PeriodUtils.getDateStringFrom(new Date(), Period.YESTERDAY) }, 
    					null, 
    					null, 
    					HistoryDbHelper.HISTORY_CREATIONDATE + " DESC");
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
	
}
