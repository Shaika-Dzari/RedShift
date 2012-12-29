package ca.n4dev.redshift;

import ca.n4dev.redshift.settings.SettingsFragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity {
	
	private static final String TAG = "SettingsActivity";
	
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
