/*
 * SettingsFragment.java
 * 
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. 
 * See http://sam.zoy.org/wtfpl/COPYING for more details. 
 * 
 * @since 2012-12-08
 */ 
package ca.n4dev.redshift;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment {
	
	private static final String TAG = "SettingsFragment";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference);
        
        Preference clearCookiePref = (Preference) findPreference(SettingsActivity.KEY_CLEARCOOKIE);
        clearCookiePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("Delete all cookies?");
				
				builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			              Log.d(TAG, "delete all!");
			           }
			       });
				
				builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               
			           }
			       });
				
				AlertDialog dialog = builder.create();
				dialog.show();
				
				return true;
			}
        });
        
        Preference clearCachePref = (Preference) findPreference(SettingsActivity.KEY_CLEARCACHE);
        clearCachePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("Delete cache?");
				
				builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			              Log.d(TAG, "delete all!");
			           }
			       });
				
				builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               
			           }
			       });
				
				AlertDialog dialog = builder.create();
				dialog.show();
				
				return true;
			}
        });
    }
}
