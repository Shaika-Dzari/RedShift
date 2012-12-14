/**
 * 
 */
package ca.n4dev.redshift.history;

import java.util.Date;

import ca.n4dev.redshift.utils.PeriodUtils;
import ca.n4dev.redshift.utils.PeriodUtils.Period;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;

/**
 * @author rguillemette
 *
 */
public class HistoryDbHelper extends SQLiteOpenHelper {

	private static final int 	DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "redshift.history.db";
    private static final String HISTORY_TABLE_NAME = "history";
    private static final String HISTORY_URL = "url";
    private static final String HISTORY_PRETTYDATE = "prettydate";
    private static final String HISTORY_CREATIONDATE = "creationdate";
    private static final String HISTORY_ID = "_id";
    private static final String HISTORY_TITLE = "title";
    
    private static final String HISTORY_TABLE_CREATE =
            "CREATE TABLE " + HISTORY_TABLE_NAME + " (" +
            		HISTORY_ID + " INTEGER PRIMARY KEY, " +
            		HISTORY_TITLE + " TEXT, " +
            		HISTORY_URL + " TEXT, " +
            		HISTORY_PRETTYDATE + " TEXT, " +
            		HISTORY_CREATIONDATE + " INTEGER);"; 
    
	public HistoryDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	
	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(HISTORY_TABLE_CREATE);
	}
	
	public Cursor queryForPeriod(SQLiteDatabase db, Period period) {
		String selection = null;
		String[] selectArgs = null;
		
		if (period != Period.INFINITY) {
			Date d = new Date();
			String pastDate = PeriodUtils.getDateStringFrom(d, period);
			selection = HISTORY_CREATIONDATE + "=?";
			selectArgs = new String[]{pastDate};
		}
		
		return db.query(HISTORY_TABLE_NAME, 
				 getHistoryTableColumns(), // columns
				 selection, //
				 selectArgs, //
				 null, // group by
				 null, //having
				 HISTORY_CREATIONDATE + " desc" //orderby
				 );
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public String[] getHistoryTableColumns() {
		String[] s = {HISTORY_ID,
					  HISTORY_TITLE,
					  HISTORY_URL,
					  HISTORY_PRETTYDATE};
		
		return s;
	}
	
	public long add(SQLiteDatabase db, String title, String url) {
		ContentValues values = new ContentValues();
		Date d = new Date();
		
		values.put(HISTORY_TITLE, title);
		values.put(HISTORY_URL, url);
		values.put(HISTORY_CREATIONDATE, DateFormat.format("yyyyMMddhhmmss", d).toString());
		values.put(HISTORY_PRETTYDATE, DateFormat.format("yyyy-MM-dd hh:mm:ss", d).toString());
		
		return db.insert(HISTORY_TABLE_NAME, null, values);
	}
	
	public void delete(SQLiteDatabase db, long id) {
		db.delete(HISTORY_TABLE_NAME, HISTORY_ID + "=?", new String[]{"" + id});
	}
}
