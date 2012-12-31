/**
 * 
 */
package ca.n4dev.redshift.persistence;

import java.util.Date;

import ca.n4dev.redshift.persistence.api.DatabaseConnection;
import ca.n4dev.redshift.persistence.api.Searchable;
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
public class HistoryDbHelper extends SQLiteOpenHelper implements Searchable, DatabaseConnection {

	private static final int 	DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "redshift.history.db";
	public static final String HISTORY_TABLE_NAME = "history";
	public static final String HISTORY_URL = "url";
	public static final String HISTORY_PRETTYDATE = "prettydate";
	public static final String HISTORY_CREATIONDATE = "creationdate";
	public static final String HISTORY_ID = "_id";
	public static final String HISTORY_TITLE = "title";
    
    private static final String HISTORY_TABLE_CREATE =
            "CREATE TABLE " + HISTORY_TABLE_NAME + " (" +
            		HISTORY_ID + " INTEGER PRIMARY KEY, " +
            		HISTORY_TITLE + " TEXT, " +
            		HISTORY_URL + " TEXT, " +
            		HISTORY_PRETTYDATE + " TEXT, " +
            		HISTORY_CREATIONDATE + " INTEGER);"; 
    
    private SQLiteDatabase db = null;
    
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

	public static String[] getHistoryTableColumns() {
		String[] s = {HISTORY_ID,
					  HISTORY_TITLE,
					  HISTORY_URL,
					  HISTORY_PRETTYDATE};
		
		return s;
	}
	
	public long add(String title, String url) {
		
		ContentValues values = new ContentValues();
		Date d = new Date();
		
		values.put(HISTORY_TITLE, title);
		values.put(HISTORY_URL, url);
		values.put(HISTORY_CREATIONDATE, DateFormat.format("yyyyMMddkkmmss", d).toString());
		values.put(HISTORY_PRETTYDATE, DateFormat.format("yyyy-MM-dd kk:mm:ss", d).toString());
		
		return db.insert(HISTORY_TABLE_NAME, null, values);
	}
	
	public void delete(long id) {
		db.delete(HISTORY_TABLE_NAME, HISTORY_ID + "=?", new String[]{"" + id});
	}
	
	public void clear() {
		db.delete(HISTORY_TABLE_NAME, null, null);
	}

	public Cursor query(Period period, Sort sort) {
		String sortClause;
		
		if (sort == Sort.BYTITLE)
			sortClause = "lower(" + HISTORY_TITLE + ") ASC";
		else if (sort == Sort.BYURL)
			sortClause = "lower(" + HISTORY_URL + ") ASC";
		else
			sortClause = HISTORY_CREATIONDATE + " DESC";
		
		return db.query(
				HISTORY_TABLE_NAME, 
				getHistoryTableColumns(), 
				HISTORY_CREATIONDATE + " >= ?", 
				new String[] { 
					PeriodUtils.getDateStringFrom(new Date(), period)
				}, 
				null, 
				null, 
				sortClause);
	}

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.events.Searchable#search(java.lang.String, ca.n4dev.redshift.utils.PeriodUtils.Period)
	 */
	@Override
	public Cursor search(String query, Period period) {
		
		String whereClause = HISTORY_CREATIONDATE + " >= ? " +
						" and ( " + HISTORY_TITLE + " like ? or " + HISTORY_URL + " like ? )";
		
		return db.query(
				HISTORY_TABLE_NAME, 
				getHistoryTableColumns(), 
				whereClause, 
				new String[] { 
					PeriodUtils.getDateStringFrom(new Date(), period), 
					"%" + query + "%", 
					"%" + query + "%" 
				}, 
				null, 
				null, 
				HISTORY_CREATIONDATE + " DESC");
		
	}

	@Override
	public void openDb() {
		this.db = getWritableDatabase();
	}

	@Override
	public void closeDb() {
		if (this.db != null)
			this.db.close();
	}
}
