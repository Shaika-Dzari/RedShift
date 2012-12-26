package ca.n4dev.redshift;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity {
	
	private static final String TAG = "SettingsActivity";
	
	public static final String KEY_HOMEPAGE = "pref_homepage"; 
	public static final String KEY_USERAGENT = "pref_useragent"; 
	public static final String KEY_HISTORY = "pref_history"; 
	public static final String KEY_COOKIE = "pref_cookie"; 
	public static final String KEY_COOKIEEXIT = "pref_cookie_exit"; 
	public static final String KEY_FORMDATA = "pref_save_formdata"; 
	public static final String KEY_SAVEPASSWD = "pref_save_passwd"; 
	public static final String KEY_JAVASCRIPT = "pref_js"; 
	public static final String KEY_LOADIMAGE = "pref_image"; 
	public static final String KEY_PLUGIN = "pref_plugin";
	
	public static final String KEY_CLEARCOOKIE = "pref_clearcookie";
	public static final String KEY_CLEARCACHE = "pref_clearcache";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        getFragmentManager().beginTransaction()
        .replace(android.R.id.content, new SettingsFragment())
        .commit();
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
}
