/**
 * 
 */
package ca.n4dev.redshift.bookmark;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;

/**
 * @author rguillemette
 *
 */
public class BookmarkDbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "redshift.bookmark.db";
    public static final String BOOKMARK_TABLE_NAME = "bookmarks";
    public static final String BOOKMARK_ID = "_id";
    public static final String BOOKMARK_TAG = "tag";
    public static final String BOOKMARK_URL = "url";
    public static final String BOOKMARK_TITLE = "title";
    public static final String BOOKMARK_CREATIONDATE = "creationdate";
    public static final String BOOKMARK_PRETTYDATE = "prettydate";
    
    private static final String BOOKMARK_TABLE_CREATE =
                "CREATE TABLE " + BOOKMARK_TABLE_NAME + " (" +
                		BOOKMARK_ID + " INTEGER PRIMARY KEY, " +
                		BOOKMARK_TITLE + " TEXT, " +
                		BOOKMARK_TAG + " TEXT, " +
                		BOOKMARK_URL + " TEXT, " +
                		BOOKMARK_PRETTYDATE + " TEXT, " +
                		BOOKMARK_CREATIONDATE + " INTEGER);";
	
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

	public long add(SQLiteDatabase db, String title, String url, String tags) {
		ContentValues values = new ContentValues();
		Date d = new Date();
		values.put(BOOKMARK_TITLE, title);
		values.put(BOOKMARK_TAG, tags);
		values.put(BOOKMARK_URL, url);
		values.put(BOOKMARK_CREATIONDATE, DateFormat.format("yyyyMMddhhmmss", d).toString());
		values.put(BOOKMARK_PRETTYDATE, DateFormat.format("yyyy-MM-dd hh:mm:ss", d).toString());
		
		return db.insert(BOOKMARK_TABLE_NAME, null, values);
	}
	
	public void delete(SQLiteDatabase db, long id) {
		db.delete(BOOKMARK_TABLE_NAME, BOOKMARK_ID + "=?", new String[]{"" + id});
	}
	
	public void insertTestData(SQLiteDatabase db) {
		
		for (int i = 0; i < 10; i ++) {
			add(db, "Web Title - test data " + i, "http://www.duckduckgo.com/?q=1+" + i, "Android, Test");			
		}
	}
	
	
	public static String[] getBookmarkTableColumns() {
		String[] s = {BOOKMARK_ID,
					  BOOKMARK_TITLE,
					  BOOKMARK_URL,
					  BOOKMARK_TAG,
					  BOOKMARK_PRETTYDATE,
					  BOOKMARK_CREATIONDATE};
		
		return s;
	}
}
