package com.teammeh.t_squaremobile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.text.format.Time;
import android.widget.Toast;

import com.tyczj.extendedcalendarview.CalendarProvider;
import com.tyczj.extendedcalendarview.Event;

public class AddAssignments {

	private static PendingIntent pendingIntent;
	private static NotificationManager notifications;
	private static SharedPreferences prefs;

	// Adds events to application calendar
	/**
	 * 
	 * @param course 
	 * @param assignment
	 * @param startYear
	 * @param startMonth
	 * @param startDay
	 * @return
	 */
	public static ContentValues addToCal(String course, String assignment,
			int startYear, int startMonth, int startDay) {
		// create object that can store values that ContentResolver can process
		ContentValues values = new ContentValues();
		values.put(CalendarProvider.COLOR, Event.COLOR_RED); // Event color
		values.put(CalendarProvider.DESCRIPTION, course); // Course name
		values.put(CalendarProvider.EVENT, assignment); // Assignment Name
		
		// Initialize calendar and time zone
		/* thhththththt	 */
		Calendar cal = Calendar.getInstance();
		TimeZone tz = TimeZone.getDefault();
		
		// set the start date and the end date of the event
		cal.set(startYear, startMonth, startDay, 0, 0);
		int startDayJulian = Time.getJulianDay(cal.getTimeInMillis(),
				TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal
						.getTimeInMillis())));
		values.put(CalendarProvider.START, cal.getTimeInMillis());
		values.put(CalendarProvider.START_DAY, startDayJulian);

		cal.set(startYear, startMonth, startDay, 0, 0);
		int endDayJulian = Time.getJulianDay(cal.getTimeInMillis(),
				TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal
						.getTimeInMillis())));

		values.put(CalendarProvider.END, cal.getTimeInMillis());
		values.put(CalendarProvider.END_DAY, endDayJulian);

		return values;
	}

	// Sets a notification for each event
	public static void setSingleNotification(Context context, String course,
			String assignment, int year, int month, int day) {
		
		// access the shared settings of the application
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		// If the variable "set_notifications" is true, then set the event
		if (prefs.getBoolean("set_notifications", true) == true) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.DAY_OF_MONTH, day);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			
			// Returns current time in milliseconds
			long currentTime = System.currentTimeMillis();
			
			// Returns Event Time in milliseconds
			long eventTime = calendar.getTimeInMillis();
			
			int noOfDays = 1; 
			
			// Gives the reminder time a day before due date
			long reminderTime = eventTime - (noOfDays * 86400000);

			// If the current time is after the event date, do not add
			// notifications
			if (currentTime < eventTime) {

				// Set alarm
				Intent myIntent = new Intent(context, AlarmReceiver.class);

				pendingIntent = PendingIntent.getBroadcast(context, 0,
						myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
				AlarmManager alarmManager = (AlarmManager) context
						.getSystemService(Context.ALARM_SERVICE);
				alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTime,
						pendingIntent);
			}
		}
		// }
	}
	
	// Cancels all notifications set by the application
	public static void cancelAllNotifications(Context context) {
		NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nMgr.cancelAll();
	}

	// Recreates all notifications if all notifications were canceled before
	public static void setAllNotifications(Context context) {
		
		Uri uri = Uri.parse((CalendarProvider.CONTENT_URI).toString());
		Cursor cursor = context.getContentResolver().query(uri,
				new String[] { CalendarProvider.END }, null, null, null);
		
		cursor.moveToFirst();
		String CName[] = new String[cursor.getCount()];
		for (int i = 0; i < CName.length; i++) {

			long eventTime = Long.parseLong(cursor.getString(0));// Returns
			Date date = new Date(eventTime);	
			Toast.makeText(context, date.toString(), Toast.LENGTH_SHORT).show();
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MONTH, date.getMonth());
			calendar.set(Calendar.YEAR, date.getYear());
			calendar.set(Calendar.DAY_OF_MONTH, date.getDay());
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			
			long eventTime2 = calendar.getTimeInMillis();// Returns Event Time in
			// milliseconds
			
			long currentTime = System.currentTimeMillis();
			
			int noOfDays = 1; // Integer.parseInt(prefs.getString("updates_notifications",
								// "null"));
			long reminderTime = eventTime2 - (noOfDays * 86400000);
			// notifications
			if (currentTime < eventTime2) {

				// Set alarm
				Intent myIntent = new Intent(context, AlarmReceiver.class);

				pendingIntent = PendingIntent.getBroadcast(context, 0,
						myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
				AlarmManager alarmManager = (AlarmManager) context
						.getSystemService(Context.ALARM_SERVICE);
				alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTime,
						pendingIntent);
			}
			cursor.moveToNext();
		}
	}

	@SuppressWarnings("deprecation")
	public static ArrayList<Items> readEvents(Context context) {
		ArrayList<Items> classAndEvent = new ArrayList<Items>();
		Uri uri = Uri.parse((CalendarProvider.CONTENT_URI).toString());
		Cursor cursor = context.getContentResolver().query(
				uri,
				new String[] { CalendarProvider.DESCRIPTION,
						CalendarProvider.EVENT, CalendarProvider.END }, null, null, null);// ,
																	// " event = ? ",
																	// null,
																	// null);
		cursor.moveToFirst();
		String CName[] = new String[cursor.getCount()];
		for (int i = 0; i < CName.length; i++) {
			Date date = new Date(Long.parseLong(cursor.getString(2)));
			classAndEvent.add(new Items(cursor.getString(0), cursor
					.getString(1), date.getDate(), date.getMonth(), date.getYear()+1900));
			CName[i] = cursor.getString(0);
			cursor.moveToNext();
		}
		return classAndEvent;
	}
	
	//I didn't use this
	public static void deleteEvent(Context context, String className,
			String assignmentName) {
		Uri uri = Uri.parse((CalendarProvider.CONTENT_URI).toString());
		ContentResolver resolver = context.getContentResolver();
		
		context.getContentResolver().delete(
				uri,
				CalendarProvider.DESCRIPTION + "=" + className + " AND "
						+ CalendarProvider.EVENT + "=" + assignmentName, null);
	}
	
	
	// Add events to google calendar
	public static void addToGoogleCalendar(Activity activity, String course, String description, 
			int year, int month, int day){
		
		//Create a calendar event to get the specific day in milliseconds
		Calendar cal = new GregorianCalendar(year, month, day);
		
		String eventUriString = "content://com.android.calendar/events";
		ContentValues values = new ContentValues();
		TimeZone tz = TimeZone.getDefault();
		values.put(Events.CALENDAR_ID, 1);
		values.put(Events.DTSTART, cal.getTimeInMillis());
		values.put(Events.DTEND, cal.getTimeInMillis());
		values.put(Events.TITLE, course + " - " + description);
		//values.put(Events.DESCRIPTION, course);
		values.put(Events.EVENT_LOCATION, "");
		values.put(Events.EVENT_TIMEZONE, tz.getID());
		values.put(Events.HAS_ALARM, 1);
		values.put(Events.ALL_DAY, 1);
		
		Uri eventUri = activity.getApplicationContext().getContentResolver().insert(Uri.parse(eventUriString), values);
		
		// Create a reminder for the calendar event
		long eventID = Long.parseLong(eventUri.getLastPathSegment());
		String reminderUriString = "content://com.android.calendar/reminders";
		
		ContentValues reminderValues = new ContentValues();
		reminderValues.put(Reminders.EVENT_ID, eventID);
		reminderValues.put(Reminders.MINUTES, 1440);
		reminderValues.put(Reminders.METHOD, 2);
		
		Uri reminderUri = activity.getApplicationContext().getContentResolver().insert(Uri.parse(reminderUriString), reminderValues);
	};
}
