package com.teammeh.t_squaremobile;

import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HomeScreenActivity extends Activity {
	// Variables to create the Navigation Drawer
	private DrawerLayout myDrawerLayout;
	private ListView myDrawerList;
	private ActionBarDrawerToggle myDrawerToggle;
	private String[] myClasses;
	private String className = "";

	// Variables to add assignments
	private String assignment_course = "";
	private String assignment_text = "";
	private String assignment_due_date = "";
	String sessionName;
	String sessionId;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);

		Uri data = getIntent().getData();
		sessionName = data.getQueryParameter("sessionName");
	    sessionId = data.getQueryParameter("sessionId");

		
		myClasses = getResources().getStringArray(R.array.class_list);
		myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		myDrawerList = (ListView) findViewById(R.id.left_drawer);

		// Implement a custom shadow that overlays main content when the
		// navigation drawer is accessed
		myDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		// Populate the navigation drawer with items and create a click listener
		myDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, myClasses));
		myDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// Enable IC_launcher icon to set action to toggle the navigation drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		myDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		myDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};

		// Listens to whenever the drawer is toggled
		myDrawerLayout.setDrawerListener(myDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_screen, menu);
		return super.onCreateOptionsMenu(menu);
	}

	// The action bar home/up action should open or close the drawer.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (myDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.action_add_assignment:
			// Add assignments
			add_assignments();
			return true;
		case R.id.action_settings:
			// Create Settings for Calendar (notifications too)
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_logout:
			// Logout of tsquare
			logout();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/* The click listener for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		// Update the content
		Intent intent;
		if (position == 0) {
		} else {
			className = myClasses[position];
			intent = new Intent(HomeScreenActivity.this, Classes.class);
			intent.putExtra("className", className);
			intent.putExtra("sessionName", sessionName);
			intent.putExtra("sessionId", sessionId);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.startActivity(intent);
		}
		// update selected item and title, then close the drawer
		myDrawerList.setItemChecked(position, true);
		myDrawerLayout.closeDrawer(myDrawerList);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		myDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		myDrawerToggle.onConfigurationChanged(newConfig);
	}

	// Create Dialog Box to enter Assignment and Due Date
	private Dialog add_assignments() {
		
		String[] courses = Arrays.copyOfRange(myClasses, 1, myClasses.length);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		// Get the layout inflater
		LayoutInflater inflater = this.getLayoutInflater();
		
		// Inflate and set the layout for the dialog
	    builder.setView(inflater.inflate(R.layout.add_assignments_dialog, null));
		
		builder.setTitle("Add Assignment");
		
		//Set Spinner

		//Set EditText
		
		//Set DatePicker

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//assignment_text = input.getText().toString();
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		return builder.show();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) { //Back key pressed
	       //Things to Do
	    	logout();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		// Do something
		super.onBackPressed();
	}

	// Create Dialog Box to Logout
	private Dialog logout() {

		// Initialize Alert Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Logout");
		builder.setMessage("Do you want to out?");
		
		// Set up the buttons
		builder.setPositiveButton("Logout",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// WRITE BACKEND CODE!!!!
						onBackPressed();
					}
				});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		return builder.show();
	}
}
