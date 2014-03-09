package com.teammeh.t_squaremobile;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.CheckBox;

public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.activity_settings);
	}

	@Override
	public void onBackPressed() {
		// Do something
		savePreferences("notifications_new_message", false);
		savePreferences("notifications_new_message_ringtone", "");
		savePreferences("notifications_new_message_vibrate", "");
		savePreferences("notifications_notification_reminder","");
		super.onBackPressed();
	}
	//Store and get setting information
		// String
	    public void savePreferences(String key, boolean value){
	    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    	Editor editor = prefs.edit();
	    	editor.putBoolean(key, value);
	    	editor.commit();
	    }
	    
	    public void savePreferences(String key, String value){
	    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    	Editor editor = prefs.edit();
	    	editor.putString(key, value);
	    	editor.commit();
	    }
}
