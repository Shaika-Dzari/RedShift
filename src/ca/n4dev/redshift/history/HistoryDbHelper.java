/**
 * 
 */
package ca.n4dev.redshift.history;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author rguillemette
 *
 */
public class HistoryDbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "redshift.history.db";
    private static final String HISTORY_TABLE_NAME = "history";
    private static final String HISTORY_URL = "url";
    private static final String HISTORY_PRETTYDATE = "prettydate";
    private static final String HISTORY_CREATIONDATE = "creationdate";
    private static final String HISTORY_ID = "id";
    
    private static final String HISTORY_TABLE_CREATE =
            "CREATE TABLE " + HISTORY_TABLE_NAME + " (" +
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
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
