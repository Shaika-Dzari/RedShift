/**
 * 
 */
package ca.n4dev.redshift.bookmark;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

/**
 * @author rguillemette
 *
 */
public class BookmarkDbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "redshift.db";
    private static final String BOOKMARK_TABLE_NAME = "bookmarks";
    private static final String BOOKMARK_URL = "url";
    private static final String BOOKMARK_CREATIONDATE = "creationdate";
    private static final String BOOKMARK_PRETTYDATE = "prettydate";
    
    private static final String BOOKMARK_TABLE_CREATE =
                "CREATE TABLE " + BOOKMARK_TABLE_NAME + " (" +
                		BOOKMARK_URL + " TEXT, " +
                		BOOKMARK_PRETTYDATE + " TEXT, " +
                		BOOKMARK_CREATIONDATE + " INTEGER);";
    
    private static final String BOOKMARK_ADD =
            "insert into " + BOOKMARK_TABLE_NAME + " (" +
            		BOOKMARK_URL + ", " +
            		BOOKMARK_PRETTYDATE + ", " +
            		BOOKMARK_CREATIONDATE + ") " +
            "values(:url, :prettyd, :insertd);";
	
	
	public BookmarkDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(BOOKMARK_TABLE_CREATE);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

	public void add(SQLiteDatabase db, String url) {
		ContentValues values = new ContentValues();
		Date d = new Date();
		values.put(BOOKMARK_URL, url);
		values.put(BOOKMARK_CREATIONDATE, d.getTime());
		values.put(BOOKMARK_PRETTYDATE, DateFormat.format("yyyy-MM-dd hh:mm:ss", d).toString());
		
		db.insert(BOOKMARK_TABLE_NAME, null, values);
	}
}
