package com.teammeh.t_squaremobile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Events;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import com.tyczj.extendedcalendarview.CalendarProvider;
import com.tyczj.extendedcalendarview.Day;
import com.tyczj.extendedcalendarview.ExtendedCalendarView;
import com.tyczj.extendedcalendarview.ExtendedCalendarView.OnDayClickListener;

public class HomeScreenActivity extends Activity {
	private static final String courseFileName = "courseFileName";
	// Variables to create the Navigation Drawer
	private DrawerLayout myDrawerLayout;
	private ListView myDrawerList;
	private ActionBarDrawerToggle myDrawerToggle;
	private String[] myClasses;
	private ArrayList<Course> courses;
	private ArrayList<Course> activeCourses;

	// Variables to add assignments
	private String assignment_course;
	private String assignment_name;
	private int assignment_day;
	private int assignment_month;
	private int assignment_year;
	private String assignment_date;
	private String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};

	// Variables for Settings, Notifications
	private SharedPreferences prefs;

	// Variables for calendar and listview
	public ExtendedCalendarView calendar;
	private ArrayAdapter adapter1;
	private ArrayList<Items> additems;
	private ListView listview;
	private ContentValues values;
	private ContentValues values2;

	// CAS Call
	String sessionName;
	String sessionId;
	GetDatabaseClassesTask dbClassesTask;
	UpdateDatabaseClassesTask updateDbClassesTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Configuration config = getResources().getConfiguration();
		if (config.smallestScreenWidthDp >= 600) {
			setContentView(R.layout.activity_home_screen_tablet);
		} else {
			setContentView(R.layout.activity_home_screen);
		}
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		calendar = (ExtendedCalendarView) findViewById(R.id.calendar);
		calendar.refreshCalendar();

		// Uri data = getIntent().getData();
		// if(data != null) {
		// if(data.getQueryParameter("sessionName") != null &&
		// data.getQueryParameter("sessionId") != null) {
		// GlobalState.setSessionName(data.getQueryParameter("sessionName"));
		// GlobalState.setSessionId(data.getQueryParameter("sessionId"));
		// }
		// }
		sessionName = GlobalState.getSessionName();
		sessionId = GlobalState.getSessionId();

		if (GlobalState.getClasses() == null) {
			myClasses = new String[1];
			myClasses[0] = "Classes loading, please wait";
			myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			myDrawerList = (ListView) findViewById(R.id.left_drawer);

			// Implement a custom shadow that overlays main content when the
			// navigation drawer is accessed
			myDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
					GravityCompat.START);

			// Populate the navigation drawer with items and create a click
			// listener
			myDrawerList.setAdapter(new ArrayAdapter<String>(this,
					R.layout.drawer_list_item, myClasses));
			// myDrawerList.setOnItemClickListener(new
			// DrawerItemClickListener());
			getDatabaseClasses(); // load preliminary screen before getting classes
			// Enable IC_launcher icon to set action to toggle the navigation
			// drawer
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
			if(GlobalState.getUserId() != null) System.out.println(GlobalState.getUserId());
			getClasses();

		} else {
			courses = GlobalState.getClasses();
			activeCourses = GlobalState.getActiveClasses();
			drawSidebar();
		}
		// while(GlobalState.getClasses() == null) {

		// }
		// courses = GlobalState.getClasses();

		// myClasses = getResources().getStringArray(R.array.class_list);
		// myClasses = new String[courses.size()];
		// for(int i = 0; i < courses.size(); i++) {
		// myClasses[i] = courses.get(i).getClassName();
		// }

		//		if (savedInstanceState == null) {
		//			selectItem(0);
		//		}

		additems = new ArrayList<Items>();

		listview = (ListView) findViewById(R.id.listView1);

		adapter1 = new MyAdapter(this, additems);// generateData());

		listview.setAdapter(adapter1);

		listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				Items selectAssignment = (Items) adapter1.getItem(position);
				final String getClass = selectAssignment.getTitle();
				final String getAssign = selectAssignment.getDescription();

				// Toast.makeText(HomeScreenActivity.this, getClass + " " +
				// getAssign, Toast.LENGTH_SHORT).show();
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						HomeScreenActivity.this);

				// set title
				alertDialogBuilder.setTitle("Delete Event");

				// set dialog message
				alertDialogBuilder
				.setMessage("Do you want to delete this event?")
				.setCancelable(false)
				.setPositiveButton("Delete",
						new DialogInterface.OnClickListener() {
					@SuppressWarnings("unchecked")
					public void onClick(DialogInterface dialog,
							int id) {
						// if this button is clicked, delete event
						Uri uri = Uri.parse((CalendarProvider.CONTENT_URI).toString());									

						String selection="("+CalendarProvider.EVENT+"="+"\""+getAssign + "\" AND " + CalendarProvider.DESCRIPTION
								+ "=\"" + getClass + "\")";
						//CalendarProvider.DESCRIPTION+"="+getClass;
						getContentResolver().delete(uri, selection , null);

						String eventUri = "content://com.android.calendar/events";
						Uri uriAndroidCal = Uri.parse((eventUri).toString());
						//String selectionAndroidCal="("+Events.TITLE+"="+"\""+getAssign + "\" AND " + CalendarProvider.DESCRIPTION
						//		+ "=\"" + getClass + "\")";
						String selectionAndroidCal="("+Events.TITLE+"="+"\""+getClass + " - " + getAssign + "\")";
						getContentResolver().delete(uriAndroidCal, selectionAndroidCal, null);

						adapter1.remove(adapter1.getItem(position));
						calendar.refreshCalendar();
						adapter1.notifyDataSetChanged();
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				});
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
				// show it
				alertDialog.show();
				return true;
			}
		});

		calendar.setOnDayClickListener(new OnDayClickListener() {

			@Override
			public void onDayClicked(AdapterView<?> adapter, View view,
					int position, long id, Day day) {

				// boolean test = false;
				//AddAssignments.setAllNotifications(getApplicationContext());

				//for (int i = 0; i < eventList.size(); i++) {
				// test = test
				// || (Arrays.asList(eventList.get(i).getTitle())
				// .contains("piggy") & Arrays.asList(
				// eventList.get(i).getDescription())
				// .contains("horse"));
				// Toast.makeText(getApplicationContext(),
				// eventList.get(i).getTitle() + " " +
				// eventList.get(i).getDescription(),
				// Toast.LENGTH_SHORT).show();
				// Toast.makeText(getApplicationContext(),
				// String.valueOf(Arrays.asList(eventList.get(i)).contains("horse")),
				// Toast.LENGTH_SHORT).show();
				//}
				//Toast.makeText(getApplicationContext(), String.valueOf(prefs.getBoolean("set_notifications", true)),
				//Toast.LENGTH_SHORT).show();
				// TODO Auto-generated method stub
				additems.clear();
				if (day.getNumOfEvenets() != 0) {
					for (int i = 0; i < day.getNumOfEvenets(); i++) {
						additems.add(new Items(day.getEvents().get(i)
								.getDescription(), day.getEvents().get(i)
								.getTitle(), day.getDay(), day.getMonth(), day
								.getYear()));
					}
					adapter1.notifyDataSetChanged();
				} else {
					additems.clear();
					adapter1.notifyDataSetChanged();
				}

			}

		});
	}

	protected void drawSidebar() {
		activeCourses = GlobalState.getActiveClasses();
		myClasses = new String[activeCourses.size()];
		for (int i = 0; i < activeCourses.size(); i++) {
			myClasses[i] = activeCourses.get(i).getClassName();
		}
		if(courses.size() == 0) {
			myClasses = new String[1];
			myClasses[0] = "Classes loading, please wait";
		}
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
//			File file = new File(this.getFilesDir(), courseFileName);
//			if (file.exists())
//				file.delete();
			
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
		case R.id.action_edit_courses:
			final ArrayList<Integer> selected = new ArrayList<Integer>();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			String[] allClasses = new String[courses.size()];
			for(int i = 0; i < allClasses.length; i++) {
				allClasses[i] = courses.get(i).getClassName();
			}
			builder.setTitle(R.string.edit_courses)
			.setMultiChoiceItems(allClasses, null, new DialogInterface.OnMultiChoiceClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					if(isChecked) {
						selected.add(which);
					} else if(selected.contains(which)) {
						selected.remove(Integer.valueOf(which));
					}
				}
			})
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					for(Integer i = 0; i < courses.size(); i++) {
						if(selected.contains(i)) {
							courses.get(i).setActive(true);
						} else courses.get(i).setActive(false);
					}
//					for(Integer i : selected) {
//						temp.add(courses.get(i));
//					}
//					courses = temp;
					GlobalState.setClasses(courses);
//					writeCoursesToFile();
					drawSidebar();
					updateDatabaseClasses(courses, true);
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			builder.create();
			builder.show();

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
		intent = new Intent(HomeScreenActivity.this, Classes.class);
		intent.putExtra("className", activeCourses.get(position).getClassName());
		intent.putExtra("sessionName", sessionName);
		intent.putExtra("sessionId", sessionId);
		intent.putExtra("classId", activeCourses.get(position).getClassId());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
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
	public void add_assignments() {

		final View addView = getLayoutInflater().inflate(
				R.layout.add_assignments_dialog, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add Assignment");
		builder.setView(addView);
		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Get EditText
				EditText edittext_course = (EditText) addView
						.findViewById(R.id.EditTextEnterCourse);
				assignment_course = edittext_course.getText().toString();
				// Get EditText
				EditText edittext_assignment = (EditText) addView
						.findViewById(R.id.EditTextAddAssignment);
				assignment_name = edittext_assignment.getText().toString();
				// Get DatePicker
				DatePicker datepicker = (DatePicker) addView
						.findViewById(R.id.datePickerAddAssignment);
				assignment_day = datepicker.getDayOfMonth();
				assignment_month = datepicker.getMonth();
				assignment_year = datepicker.getYear();
				assignment_date = "Due Date: " + months[assignment_month] +
						" " + assignment_day + ", " + assignment_year;
				// ArrayList<String> eventList =
				// AddAssignments.readEvents(getApplicationContext());
				boolean checkEvent = false;
				ArrayList<Items> eventList = AddAssignments
						.readEvents(getApplicationContext());

				for (int i = 0; i < eventList.size(); i++) {
					checkEvent = checkEvent
							|| (Arrays.asList(eventList.get(i).getTitle())
									.contains(assignment_course) & Arrays
									.asList(eventList.get(i).getDescription())
									.contains(assignment_name) 
									& Arrays.asList(eventList.get(i).getDate())
									.contains(assignment_date));
				}

				if (checkEvent == false) {
					values = AddAssignments.addToCal(assignment_course,
							assignment_name, assignment_year, assignment_month,
							assignment_day);
					//Date date = new Date(assignment_year-1900, assignment_month, assignment_day);
					//Toast.makeText(getApplicationContext(), date.toString(), Toast.LENGTH_LONG).show();
					//Add event to google calendar
					AddAssignments.addToGoogleCalendar(HomeScreenActivity.this,
							assignment_course, assignment_name, assignment_year, 
							assignment_month, assignment_day);
					//Uri uri2 = getContentResolver().insert(
					//		Uri.parse("content://com.android.calendar/events"), values2);

					Uri uri = getContentResolver().insert(
							CalendarProvider.CONTENT_URI, values);
					// calendar = (ExtendedCalendarView)
					// findViewById(R.id.calendar);

					AddAssignments.setSingleNotification(HomeScreenActivity.this,
							assignment_course, assignment_name,
							assignment_year, assignment_month, assignment_day);
					calendar.refreshCalendar();
				}
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
		if ((keyCode == KeyEvent.KEYCODE_BACK)) { // Back key pressed
			// Things to Do
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

	protected void getUserId() {
		String url = "http://dev.m.gatech.edu/d/tkerr3/w/t2/content/api/getUserInfo";
		HttpGet get = new HttpGet(url);
		new GetUserIdTask().execute(get);
	}

	protected void getClasses() {
//		File file = new File(this.getFilesDir(), courseFileName);
//		boolean readResult = false;
//		if (file.exists()) {
//			readResult = readCoursesFromFile();
//		}
//		if (readResult) {
//			this.courses = GlobalState.getClasses();
//			System.out.println("I'm in here now! "
//					+ courses.get(0).getClassName());
//			drawSidebar();
//		} else {
			String url = "http://dev.m.gatech.edu/d/tkerr3/w/t2/content/api/siteJson";
			HttpGet get = new HttpGet(url);
			new GetClassesTask().execute(get);
//		}
	}

	protected void getDatabaseClasses() {
		String url = "http://dev.m.gatech.edu/d/tkerr3/w/t2/content/api/getDatabaseSites";
		HttpPost post = new HttpPost(url);
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		if(GlobalState.getUserId() == null) {
			getUserId();
		}
		while(GlobalState.getUserId() == null);
		System.out.println("User id loaded and querying db");
		postParameters.add(new BasicNameValuePair("userId", GlobalState.getUserId()));
		try {
			post.setEntity(new UrlEncodedFormEntity(postParameters));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dbClassesTask = new GetDatabaseClassesTask();
		dbClassesTask.execute(post);


	}

	// }

	protected void parseJson(JSONObject jObject) {
		ArrayList<Course> classes = new ArrayList<Course>();
		JSONArray array = null;
		try {
			array = jObject.getJSONArray("site_collection");
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				String classTitle = obj.getString("title");
				String classId = obj.getString("id");
				classes.add(new Course(classTitle, classId, true));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(GlobalState.getClasses() != null) {
			if(GlobalState.getClasses().size() == 0) {
				GlobalState.setClasses(classes);
				this.courses = classes;
				this.activeCourses = GlobalState.getActiveClasses();
//				File file = new File(this.getFilesDir(), courseFileName);
//				if (!file.exists())
//					writeCoursesToFile();
				drawSidebar();
			}
		}
		updateDatabaseClasses(classes, false);
		
	}
	
	protected void updateDatabaseClasses(ArrayList<Course> classes, boolean updateExisting) {
		JSONArray array = new JSONArray();
		try {
			for(Course course : classes) {
				JSONObject obj = new JSONObject();
				obj.put("site_id", course.getClassId());
				obj.put("user_id", GlobalState.getUserId());
				obj.put("site_title", course.getClassName());
				String active = "0";
				if(course.isActive()) active = "1";
				obj.put("active", active);
				array.put(obj);
			}

			String jsonString = array.toString();
			//			StringEntity jsonEntity = null;
			//			jsonEntity = new StringEntity(jsonString);
			String url = "";
			if(updateExisting) {
				url = "http://dev.m.gatech.edu/d/tkerr3/w/t2/content/api/updateSitesOnDatabase";
			} else url = "http://dev.m.gatech.edu/d/tkerr3/w/t2/content/api/insertSites";
			HttpPost post = new HttpPost(url);
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("siteData", jsonString));
			post.setEntity(new UrlEncodedFormEntity(postParameters));
			//			post.setHeader("Content-type", "application/json");
			updateDbClassesTask = new UpdateDatabaseClassesTask();
			updateDbClassesTask.execute(post);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	protected void parseDatabaseClassesJSON(JSONArray jArray) {
		ArrayList<Course> classes = new ArrayList<Course>();
		for(int i = 0; i < jArray.length(); i++) {
			try {
				JSONObject obj = jArray.getJSONObject(i);
				Course course = new Course(obj.optString("site_title"),
						obj.optString("site_id"), obj.optString("active").equals("1"));
				classes.add(course);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		GlobalState.setClasses(classes);
		this.courses = classes;
		this.activeCourses = GlobalState.getActiveClasses();
		drawSidebar();
		
	}

	protected void parseUserJson(JSONObject jObject) {
		GlobalState.setUserId(jObject.optString("id"));
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
				reader = new BufferedReader(new InputStreamReader(stream,
						"UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				result = sb.toString();
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
				try {
					if (stream != null)
						stream.close();
				} catch (Exception squish) {
				}
			}
			return jObject;
		}

		@Override
		protected JSONObject doInBackground(HttpGet... params) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet get = params[0];
			get.setHeader("Cookie", sessionName + "=" + sessionId);
			HttpEntity entity = null;
			HttpResponse response = null;
			JSONObject jObject = null;
			try {
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
			parseJson(jObject);
		}
	}

	public class GetDatabaseClassesTask extends
	AsyncTask<HttpPost, String, JSONArray> {

		String mText;

		protected JSONArray extractJson(HttpEntity entity) {
			InputStream stream = null;
			BufferedReader reader;
			String line = "";
			String result = "";
			JSONArray jArray = null;

			try {
				stream = entity.getContent();
				reader = new BufferedReader(new InputStreamReader(stream,
						"UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				result = sb.toString();
				if(result.equals("")) {
					jArray = new JSONArray();
				} else jArray = new JSONArray(result);
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
				try {
					if (stream != null)
						stream.close();
				} catch (Exception squish) {
				}
			}
			return jArray;
		}

		@Override
		protected JSONArray doInBackground(HttpPost... params) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = params[0];
			post.setHeader("Cookie", sessionName + "=" + sessionId);
			HttpEntity entity = null;
			HttpResponse response = null;
			JSONArray jArray = null;
			try {
				response = client.execute(post);
				entity = response.getEntity();
				if (response.getStatusLine().toString().contains("504")) {
					jArray = null;
				} else {
					jArray = extractJson(entity);
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			client.getConnectionManager().shutdown();

			return jArray;

		}

		@Override
		protected void onPostExecute(JSONArray jArray) {
			if (jArray != null)
				parseDatabaseClassesJSON(jArray);
			// if(jArray != null && jArray.length() > 0) {
			// TextView t = (TextView)findViewById(R.id.textView1);
			// try {
			// t.setText(jArray.getString(0));
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }
		}
	}

	public class GetUserIdTask extends AsyncTask<HttpGet, String, JSONObject> {

		String mText;

		protected JSONObject extractJson(HttpEntity entity) {
			InputStream stream = null;
			BufferedReader reader;
			String line = "";
			String result = "";
			JSONObject jObject = null;

			try {
				stream = entity.getContent();
				reader = new BufferedReader(new InputStreamReader(stream,
						"UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				result = sb.toString();
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
				try {
					if (stream != null)
						stream.close();
				} catch (Exception squish) {
				}
			}
			return jObject;
		}

		@Override
		protected JSONObject doInBackground(HttpGet... params) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet get = params[0];
			get.setHeader("Cookie", sessionName + "=" + sessionId);
			HttpEntity entity = null;
			HttpResponse response = null;
			JSONObject jObject = null;
			try {
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
			parseUserJson(jObject);
			return jObject;

		}

		@Override
		protected void onPostExecute(JSONObject jObject) {
//			super.onPostExecute(jObject);
//			parseUserJson(jObject);
		}
	}

	public class UpdateDatabaseClassesTask extends AsyncTask<HttpPost, Integer, Integer> {

		@Override
		protected Integer doInBackground(HttpPost... params) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = params[0];
			post.setHeader("Cookie", sessionName + "=" + sessionId);
			HttpResponse response = null;
			int statusCode = 0;
			try {
				response = client.execute(post);
				statusCode = response.getStatusLine().getStatusCode();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			client.getConnectionManager().shutdown();

			return statusCode;
		}

		@Override
		protected void onPostExecute(Integer statusCode) {
//			System.out.println("The status is: " + statusCode);
		}

	}


//	protected boolean writeCoursesToFile() {
//		boolean result = false;
//		try {
//			FileOutputStream fos = this.openFileOutput(courseFileName,
//					Context.MODE_PRIVATE);
//			ObjectOutputStream oos = new ObjectOutputStream(fos);
//			oos.writeObject(GlobalState.getClasses());
//			result = true;
//			System.out.println("File written!");
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return result;
//	}

//	protected boolean readCoursesFromFile() {
//		boolean result = false;
//		File file = new File(this.getFilesDir(), courseFileName);
//		if (file.exists()) {
//			try {
//				FileInputStream fis = this.openFileInput(courseFileName);
//				ObjectInputStream ois = new ObjectInputStream(fis);
//				ArrayList<Course> courseList = (ArrayList<Course>) ois
//						.readObject();
//				if (courseList != null) {
//					GlobalState.setClasses(courseList);
//					this.activeCourses = GlobalState.getActiveClasses();
//					result = true;
//					System.out.println("File loaded!");
//				}
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//		}
//		return result;
//	}

	// Add events to the calendar
	/*
	 * private void add_to_Cal(String course, String assignment, int startYear,
	 * int startMonth, int startDay){ ContentValues values = new
	 * ContentValues(); values.put(CalendarProvider.COLOR, Event.COLOR_RED);
	 * values.put(CalendarProvider.DESCRIPTION, course);
	 * values.put(CalendarProvider.EVENT, assignment);
	 * 
	 * Calendar cal = Calendar.getInstance(); TimeZone tz =
	 * TimeZone.getDefault();
	 * 
	 * cal.set(startYear, startMonth, startDay, 0, 0); int startDayJulian =
	 * Time.getJulianDay(cal.getTimeInMillis(),
	 * TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal.getTimeInMillis())));
	 * values.put(CalendarProvider.START, cal.getTimeInMillis());
	 * values.put(CalendarProvider.START_DAY, startDayJulian);
	 * 
	 * cal.set(startYear, startMonth, startDay, 0, 0); int endDayJulian =
	 * Time.getJulianDay(cal.getTimeInMillis(),
	 * TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal.getTimeInMillis())));
	 * 
	 * values.put(CalendarProvider.END, cal.getTimeInMillis());
	 * values.put(CalendarProvider.END_DAY, endDayJulian);
	 * 
	 * Uri uri = getContentResolver().insert(CalendarProvider.CONTENT_URI,
	 * values); }
	 */

	// Alarm receiver
	/*
	 * private void setNotification(String course, String assignment, int year,
	 * int month, int day){
	 * 
	 * //if (Integer.parseInt(prefs.getString("updates_notifications", "null"))
	 * != 0){ //String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
	 * "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"}; //String date = months[month]
	 * + " " + day + ", " + year;
	 * 
	 * Calendar calendar = Calendar.getInstance(); calendar.set(Calendar.MONTH,
	 * month); calendar.set(Calendar.YEAR, year);
	 * calendar.set(Calendar.DAY_OF_MONTH, day);
	 * calendar.set(Calendar.HOUR_OF_DAY, 0); calendar.set(Calendar.MINUTE, 0);
	 * calendar.set(Calendar.SECOND, 0);
	 * 
	 * long eventTime=calendar.getTimeInMillis();//Returns Time in milliseconds
	 * 
	 * int noOfDays=1;
	 * //Integer.parseInt(prefs.getString("updates_notifications", "null"));
	 * long reminderTime=eventTime-(noOfDays*86400000);//Time in milliseconds
	 * when the alarm will shoot up & you do not need to concider month/year
	 * with this approach as time is already in milliseconds.
	 * 
	 * //Set alarm Intent myIntent = new Intent(HomeScreenActivity.this,
	 * AlarmReceiver.class); ///myIntent.putExtra("course", course);
	 * ///myIntent.putExtra("assignment", assignment);
	 * ///myIntent.putExtra("date", date);
	 * 
	 * pendingIntent = PendingIntent.getBroadcast(HomeScreenActivity.this, 0,
	 * myIntent,PendingIntent.FLAG_CANCEL_CURRENT); AlarmManager alarmManager =
	 * (AlarmManager)getSystemService(ALARM_SERVICE);
	 * alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
	 * //} }
	 */
}
