/**
 * 
 */
package ca.n4dev.redshift.bookmark;

import java.util.Date;

import ca.n4dev.redshift.events.Searchable;
import ca.n4dev.redshift.history.HistoryDbHelper;
import ca.n4dev.redshift.utils.PeriodUtils;
import ca.n4dev.redshift.utils.PeriodUtils.Period;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;
import android.util.Log;

/**
 * @author rguillemette
 *
 */
public class BookmarkDbHelper extends SQLiteOpenHelper implements Searchable {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "redshift.bookmark.db";
    public static final String BOOKMARK_TABLE_NAME = "bookmarks";
    public static final String BOOKMARK_ID = "_id";
    public static final String BOOKMARK_TAG = "tag";
    public static final String BOOKMARK_URL = "url";
    public static final String BOOKMARK_TITLE = "title";
    public static final String BOOKMARK_CREATIONDATE = "creationdate";
    public static final String BOOKMARK_PRETTYDATE = "prettydate";
    public static final String BOOKMARK_SHOWINHOME = "showinhome";
    
    public enum Sort {
    	BYTITLE, BYURL, BYDATE
    }
    
    private static final String BOOKMARK_TABLE_CREATE =
                "CREATE TABLE " + BOOKMARK_TABLE_NAME + " (" +
                		BOOKMARK_ID + " INTEGER PRIMARY KEY, " +
                		BOOKMARK_TITLE + " TEXT, " +
                		BOOKMARK_TAG + " TEXT, " +
                		BOOKMARK_URL + " TEXT, " +
                		BOOKMARK_PRETTYDATE + " TEXT, " +
                		BOOKMARK_SHOWINHOME + " INTEGER, " +
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

	public long add(SQLiteDatabase db, String title, String url, String tags, String showinhome) {
		ContentValues values = new ContentValues();
		Date d = new Date();
		values.put(BOOKMARK_TITLE, title);
		values.put(BOOKMARK_TAG, tags);
		values.put(BOOKMARK_URL, url);
		values.put(BOOKMARK_SHOWINHOME, showinhome);
		values.put(BOOKMARK_CREATIONDATE, DateFormat.format("yyyyMMddkkmmss", d).toString());
		values.put(BOOKMARK_PRETTYDATE, DateFormat.format("yyyy-MM-dd kk:mm:ss", d).toString());
		
		return db.insert(BOOKMARK_TABLE_NAME, null, values);
	}
	
	public void delete(SQLiteDatabase db, long id) {
		db.delete(BOOKMARK_TABLE_NAME, BOOKMARK_ID + "=?", new String[]{"" + id});
	}
	
	public void insertTestData(SQLiteDatabase db) {
		
		for (int i = 0; i < 10; i ++) {
			add(db, "Web Title - test data " + i, "http://www.duckduckgo.com/?q=1+" + i, "Android, Test", "" + (i % 2));			
		}
	}
	
	
	public static String[] getBookmarkTableColumns() {
		String[] s = {BOOKMARK_ID,
					  BOOKMARK_TITLE,
					  BOOKMARK_URL,
					  BOOKMARK_TAG,
					  BOOKMARK_PRETTYDATE,
					  BOOKMARK_SHOWINHOME,
					  BOOKMARK_CREATIONDATE};
		
		return s;
	}
	
	public Cursor queryAll(SQLiteDatabase db, Sort sort) {
		
		String sortClause;
		
		if (sort == Sort.BYTITLE)
			sortClause = "lower(" + BOOKMARK_TITLE + ") ASC";
		else if (sort == Sort.BYURL)
			sortClause = "lower(" + BOOKMARK_URL + ") ASC";
		else
			sortClause = BOOKMARK_CREATIONDATE + " DESC";
		
		return db.query(BookmarkDbHelper.BOOKMARK_TABLE_NAME,
				BookmarkDbHelper.getBookmarkTableColumns(), 
				null, 
				null, 
				null, 
				null, 
				sortClause);
	}
	
	public Cursor queryAll(SQLiteDatabase db) {
		return queryAll(db, Sort.BYDATE);
	}
	

	/* (non-Javadoc)
	 * @see ca.n4dev.redshift.events.Searchable#search(java.lang.String, ca.n4dev.redshift.utils.PeriodUtils.Period)
	 */
	@Override
	public Cursor search(SQLiteDatabase db, String query, Period period) {
		String whereClause = BOOKMARK_CREATIONDATE + " >= ? " +
							" and (" + BOOKMARK_TAG + " like ? or " +
									   BOOKMARK_TITLE + " like ? or " +
									   BOOKMARK_URL  + " like ? )";
		
		return db.query(BOOKMARK_TABLE_NAME, 
						 getBookmarkTableColumns(), 
						 whereClause, 
						 new String[] {
							PeriodUtils.getDateStringFrom(new Date(), period), 
							"%" + query + "%", 
							"%" + query + "%", 
							"%" + query + "%"  
						 }, 
						 null, 
						 null, 
						 BOOKMARK_CREATIONDATE + " DESC");
		
	}
	
	public void delete(SQLiteDatabase db, Long bookmarkId) {
		db.delete(BOOKMARK_TABLE_NAME, BOOKMARK_ID + " = ?", new String[]{"" + bookmarkId});
	}
}
