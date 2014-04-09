package com.teammeh.t_squaremobile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.tyczj.extendedcalendarview.CalendarProvider;
import com.tyczj.extendedcalendarview.Day;
import com.tyczj.extendedcalendarview.Event;
import com.tyczj.extendedcalendarview.ExtendedCalendarView;
import com.tyczj.extendedcalendarview.ExtendedCalendarView.OnDayClickListener;

public class HomeScreenActivity extends Activity {
	private static final String courseFileName = "courseFileName";
	// Variables to create the Navigation Drawer
	private DrawerLayout myDrawerLayout;
	private ListView myDrawerList;
	private ActionBarDrawerToggle myDrawerToggle;
	private String[] myClasses;
	private String className = "";
	private ArrayList<Course> courses;

	// Variables to add assignments
	private String assignment_course;
	private String assignment_name;
	private int assignment_day;
	private int assignment_month;
	private int assignment_year;

	// Variables for Settings, Notifications
	private SharedPreferences prefs;
	private boolean enable_notis;
	private int reminder_notis;
	private String ringtone_notis;
	private boolean vibrate_notis;

	//Variables for calendar and listview
	private ExtendedCalendarView calendar;
	private ArrayAdapter adapter1;
	private ArrayList<Items> additems;
	private ListView listview;

	// CAS Call
	String sessionName;
	String sessionId;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		calendar = (ExtendedCalendarView) findViewById(R.id.calendar);

//		Uri data = getIntent().getData();
//		if(data != null) {
//			if(data.getQueryParameter("sessionName") != null && data.getQueryParameter("sessionId") != null) {
//				GlobalState.setSessionName(data.getQueryParameter("sessionName"));
//				GlobalState.setSessionId(data.getQueryParameter("sessionId"));
//			}
//		}
		sessionName = GlobalState.getSessionName();
		sessionId = GlobalState.getSessionId();


		if(GlobalState.getClasses() == null) {
			myClasses = new String[1];
			myClasses[0] = "Classes loading, please wait";
			myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			myDrawerList = (ListView) findViewById(R.id.left_drawer);

			// Implement a custom shadow that overlays main content when the
			// navigation drawer is accessed
			myDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
					GravityCompat.START);

			// Populate the navigation drawer with items and create a click listener
			myDrawerList.setAdapter(new ArrayAdapter<String>(this,
					R.layout.drawer_list_item, myClasses));
			//			myDrawerList.setOnItemClickListener(new DrawerItemClickListener());
			getClasses(); // load preliminary screen before getting classes
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
			
		} else {
			courses = GlobalState.getClasses();
			drawSidebar();
		}
		//		while(GlobalState.getClasses() == null) {

		//		}
		//		courses = GlobalState.getClasses();





		//		myClasses = getResources().getStringArray(R.array.class_list);
		//		myClasses = new String[courses.size()];
		//		for(int i = 0; i < courses.size(); i++) {
		//			myClasses[i] = courses.get(i).getClassName();
		//		}


		if (savedInstanceState == null) {
			selectItem(0);
		}
		
		additems = new ArrayList<Items>();
		
		this.listview = (ListView) findViewById(R.id.listView1);
		
		adapter1 = new MyAdapter(this, additems);//generateData());
		
		listview.setAdapter(adapter1);
		
		calendar.setOnDayClickListener(new OnDayClickListener() {

			@Override
			public void onDayClicked(AdapterView<?> adapter, View view,
					int position, long id, Day day) {
				// TODO Auto-generated method stub
				additems.clear();
				if (day.getNumOfEvenets() != 0){
					for (int i = 0; i < day.getNumOfEvenets(); i++) {
						additems.add(new Items(day.getEvents().get(i)
								.getDescription(), day.getEvents().get(i)
								.getTitle(), day.getDay(), day.getMonth(), day.getYear()));
					}
					adapter1.notifyDataSetChanged();
				}
				else{
					additems.clear();
					adapter1.notifyDataSetChanged();
				}
			}
			
		});
	}

	protected void drawSidebar() {
		myClasses = new String[courses.size()];
		for(int i = 0; i < courses.size(); i++) {
			myClasses[i] = courses.get(i).getClassName();
		}
		System.out.println(myClasses[0]);
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

	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString("sessionName", sessionName);
		savedInstanceState.putString("sessionId", sessionId);
		super.onSaveInstanceState(savedInstanceState);
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
		case R.id.action_refreshlist:
			// Force refresh of course list
			File file = new File(this.getFilesDir(), courseFileName);
			if(file.exists()) file.delete();
			getClasses();
			return true;
		case R.id.action_settings:
			// Create Settings for Calendar (notifications too)
			Intent intent2 = new Intent(this, SettingsActivity.class);
			startActivity(intent2);
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
			intent = new Intent(HomeScreenActivity.this, Classes.class);
			intent.putExtra("className", courses.get(position).getClassName());
			intent.putExtra("sessionName", sessionName);
			intent.putExtra("sessionId", sessionId);
			intent.putExtra("classId", courses.get(position).getClassId());
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
	public void add_assignments(){

		final View addView = getLayoutInflater().inflate(R.layout.add_assignments_dialog, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add Assignment");
		builder.setView(addView);
		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//Get EditText
				EditText edittext_course = (EditText)addView.findViewById(R.id.EditTextEnterCourse);
				assignment_course = edittext_course.getText().toString();
				// Get EditText
				EditText edittext_assignment = (EditText)addView.findViewById(R.id.EditTextAddAssignment);
				assignment_name = edittext_assignment.getText().toString();
				// Get DatePicker
				DatePicker datepicker = (DatePicker)addView.findViewById(R.id.datePickerAddAssignment);
				assignment_day = datepicker.getDayOfMonth();
				assignment_month = datepicker.getMonth();
				assignment_year = datepicker.getYear();
				add_to_Cal(assignment_course, assignment_name, assignment_year, assignment_month, assignment_day);
				calendar = (ExtendedCalendarView) findViewById(R.id.calendar);
				calendar.refreshCalendar();
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.show();
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
		// Close Application
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		super.onBackPressed();
	}

	// Create Dialog Box to Logout
	private Dialog logout() {

		// Initialize Alert Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Logout");
		builder.setMessage("Do you want to logout?");

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

	protected void getClasses() {
		File file = new File(this.getFilesDir(), courseFileName);
		boolean readResult = false;
		if(file.exists()) {
			readResult = readCoursesFromFile();
		} 
		if(readResult) {
			this.courses = GlobalState.getClasses();
			System.out.println("I'm in here now! " + courses.get(0).getClassName());
			drawSidebar();
		} else {
			String url = "http://dev.m.gatech.edu/d/tkerr3/w/t2/content/api/siteJson";
			HttpGet get = new HttpGet(url);
			new GetClassesTask().execute(get);
		}
	}
	//	}

	protected void parseJson(JSONObject jObject) {
		ArrayList<Course> classes = new ArrayList<Course>();
		JSONArray array = null;
		try {
			//			System.out.println(jObject.length());
			array = jObject.getJSONArray("site_collection");
			for(int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				String classTitle = obj.getString("title");
				String classId = obj.getString("id");
				classes.add(new Course(classTitle, classId));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GlobalState.setClasses(classes);
		this.courses = classes;
		File file = new File(this.getFilesDir(), courseFileName);
		if(!file.exists()) writeCoursesToFile();
		//		if(!prefs.contains("courseListJson")) {
		//			SharedPreferences.Editor editor = prefs.edit();
		//			editor.putString("courseListJson", jObject.toString());
		//			editor.commit();
		//		}
		//		if(!prefs.contains("courseNames") && !prefs.contains("courseIds")) {
		//			HashSet<String> courseNames = new HashSet<String>();
		//			HashSet<String> courseIds = new HashSet<String>();
		//			for(Course course : courses) {
		//				courseNames.add(course.getClassName());
		//				courseIds.add(course.getClassId());
		//			}
		//			SharedPreferences.Editor editor = prefs.edit();
		//			editor.putStringSet("courseNames", courseNames);
		//			editor.putStringSet("courseIds", courseIds);
		//			editor.commit();
		//			
		//		}
		drawSidebar();
	}

	public class GetClassesTask extends AsyncTask<HttpGet, String, JSONObject> {

		String mText;

		protected JSONObject extractJson(HttpEntity entity) {
			InputStream stream = null;
			BufferedReader reader;
			String line = "";
			String result = "";
			JSONObject jObject = null;

			try {
				stream = entity.getContent();
				reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				while ((line = reader.readLine()) != null)
				{
					sb.append(line + "\n");
				}
				result = sb.toString();
				//				System.out.println(result);
				jObject = new JSONObject(result);
				jObject = new JSONObject(jObject.getString("body"));
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try{if(stream != null)stream.close();}catch(Exception squish){}
			}
			return jObject;
		}


		@Override
		protected JSONObject doInBackground(HttpGet... params) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet get = params[0];
			get.setHeader("Cookie", sessionName+"="+sessionId);
			HttpEntity entity = null;
			HttpResponse response = null;
			JSONObject jObject = null;	    	    
			try {
				Header[] headers = get.getAllHeaders();
				for(Header header : headers) {
					//					System.out.println(header.getName() + " " + header.getValue());
				}
				response = client.execute(get);
				entity = response.getEntity();
				jObject = extractJson(entity);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			client.getConnectionManager().shutdown();

			return jObject;

		}

		@Override
		protected void onPostExecute(JSONObject jObject) {
			//			System.out.println("I'm here!");
			parseJson(jObject);
		}
	}

	protected boolean writeCoursesToFile() {
		boolean result = false;
		try {
			FileOutputStream fos = this.openFileOutput(courseFileName, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(GlobalState.getClasses());
			result = true;
			System.out.println("File written!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	protected boolean readCoursesFromFile() {
		boolean result = false;
		File file = new File(this.getFilesDir(), courseFileName);
		if(file.exists()) {
			try {
				FileInputStream fis = this.openFileInput(courseFileName);
				ObjectInputStream ois = new ObjectInputStream(fis);
				ArrayList<Course> courseList = (ArrayList<Course>) ois.readObject();
				if(courseList != null) {
					GlobalState.setClasses(courseList);
					result = true;
					System.out.println("File loaded!");
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}



	// Add events to the calendar
	private void add_to_Cal(String course, String assignment, int startYear, int startMonth, int startDay){
		ContentValues values = new ContentValues();
		values.put(CalendarProvider.COLOR, Event.COLOR_RED);
		values.put(CalendarProvider.DESCRIPTION, course);
		values.put(CalendarProvider.EVENT, assignment);

		Calendar cal = Calendar.getInstance();
		TimeZone tz = TimeZone.getDefault();

		cal.set(startYear, startMonth, startDay, 0, 0);
		int startDayJulian = Time.getJulianDay(cal.getTimeInMillis(), TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal.getTimeInMillis())));
		values.put(CalendarProvider.START, cal.getTimeInMillis());
		values.put(CalendarProvider.START_DAY, startDayJulian);

		cal.set(startYear, startMonth, startDay, 0, 0);
		int endDayJulian = Time.getJulianDay(cal.getTimeInMillis(), TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal.getTimeInMillis())));

		values.put(CalendarProvider.END, cal.getTimeInMillis());
		values.put(CalendarProvider.END_DAY, endDayJulian);

		Uri uri = getContentResolver().insert(CalendarProvider.CONTENT_URI, values);
	}
	
	//Alarm receiver
	//private void setNotification(int day, int month, int year){
	//	Calendar noteCal = Calendar.getInstance();
	//	//noteCal.get
	//}
}
