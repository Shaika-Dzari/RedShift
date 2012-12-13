/*
 * BookmarkEditActivity.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-12-10
 */ 
package ca.n4dev.redshift.bookmark;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

public class BookmarkEditActivity extends Activity {
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            BookmarkEditFragment edit = new BookmarkEditFragment();
            edit.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(android.R.id.content, edit).commit();
        }
    }
}
