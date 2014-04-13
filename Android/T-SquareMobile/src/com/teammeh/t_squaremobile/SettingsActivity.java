package com.teammeh.t_squaremobile;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity {
	SharedPreferences prefs;
	 @Override
	    public void onCreate(Bundle savedInstanceState) {        
	        super.onCreate(savedInstanceState);        
	        addPreferencesFromResource(R.layout.preferences);   
	        prefs = PreferenceManager.getDefaultSharedPreferences(this);

	        OnPreferenceClickListener btnListener = new OnPreferenceClickListener() {
	            @Override
	            public boolean onPreferenceClick(Preference preference) {
	                final String key = preference.getKey();

	                if (key.equals("set_notifications")) {
	                	if (prefs.getBoolean("set_notifications", true) == true) {
	                		//Toast.makeText(getApplicationContext(), String.valueOf(prefs.getBoolean("set_notifications", true)), Toast.LENGTH_SHORT).show();
	                		AddAssignments.setAllNotifications(getApplicationContext());
	                		return true; // we handled the click
	                	}
	                	else{
	                		AddAssignments.cancelAllNotifications(getApplicationContext());
	                		//Toast.makeText(getApplicationContext(), String.valueOf(prefs.getBoolean("set_notifications", true)), Toast.LENGTH_SHORT).show();
	                		return true; // we handled the click
	                	}
	                }
	                return false; // we didn't handle the click
	            }
	        };
	        Preference prefBtn = findPreference("set_notifications");
	        prefBtn.setOnPreferenceClickListener(btnListener);
	    }
	 
	 
}
