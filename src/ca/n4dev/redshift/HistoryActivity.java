package ca.n4dev.redshift;

import ca.n4dev.redshift.history.HistoryAdapter;
import ca.n4dev.redshift.history.HistoryDbHelper;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class HistoryActivity extends Activity {
	
	private HistoryDbHelper historyHelper;
	private SQLiteDatabase historyDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        historyHelper = new HistoryDbHelper(this);
        historyDatabase = historyHelper.getReadableDatabase();
        
        ListView lv = (ListView) findViewById(R.id.lstHistory);
        
        Cursor c = this.historyDatabase.query(
        					HistoryDbHelper.HISTORY_TABLE_NAME, 
        					HistoryDbHelper.getHistoryTableColumns(), 
        					null, 
        					null, 
        					null, 
        					null, 
        					HistoryDbHelper.HISTORY_CREATIONDATE + " DESC");
        
        lv.setAdapter(new HistoryAdapter(this, c));
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
