package ca.n4dev.redshift;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class BookmarkActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_bookmark, menu);
        return true;
    }
}
