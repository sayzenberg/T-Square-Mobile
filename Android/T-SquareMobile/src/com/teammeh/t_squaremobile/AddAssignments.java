package com.teammeh.t_squaremobile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

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
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.widget.Toast;

import com.tyczj.extendedcalendarview.CalendarProvider;
import com.tyczj.extendedcalendarview.Event;

public class AddAssignments {

	private static PendingIntent pendingIntent;
	private static NotificationManager notifications;
	private static SharedPreferences prefs;

	public static ContentValues addToCal(String course, String assignment,
			int startYear, int startMonth, int startDay) {
		ContentValues values = new ContentValues();
		values.put(CalendarProvider.COLOR, Event.COLOR_RED);
		values.put(CalendarProvider.DESCRIPTION, course);
		values.put(CalendarProvider.EVENT, assignment);

		Calendar cal = Calendar.getInstance();
		TimeZone tz = TimeZone.getDefault();

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

		// Uri uri = getContentResolver().insert(CalendarProvider.CONTENT_URI,
		// values);
		return values;
	}

	public static void setSingleNotification(Context context, String course,
			String assignment, int year, int month, int day) {

		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		// if (Integer.parseInt(prefs.getString("updates_notifications",
		// "null")) != 0){
		// String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
		// "Aug", "Sept", "Oct", "Nov", "Dec"};
		// String date = months[month] + " " + day + ", " + year;
		if (prefs.getBoolean("set_notifications", true) == true) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.DAY_OF_MONTH, day);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);

			long currentTime = System.currentTimeMillis();

			long eventTime = calendar.getTimeInMillis();// Returns Event Time in
														// milliseconds
			int noOfDays = 1; // Integer.parseInt(prefs.getString("updates_notifications",
								// "null"));
			long reminderTime = eventTime - (noOfDays * 86400000);// Time in
			//milliseconds

			// If the current time is after the event date, do not add
			// notifications
			if (currentTime < eventTime) {

				// Set alarm
				Intent myIntent = new Intent(context, AlarmReceiver.class);
				// /myIntent.putExtra("course", course);
				// /myIntent.putExtra("assignment", assignment);
				// /myIntent.putExtra("date", date);

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

	public static void cancelAllNotifications() {
		notifications.cancelAll();
	}

	public static void setAllNotifications(Context context) {
		// ArrayList<Items> classAndEvent = new ArrayList<Items>();
		Uri uri = Uri.parse((CalendarProvider.CONTENT_URI).toString());
		Cursor cursor = context.getContentResolver().query(uri,
				new String[] { CalendarProvider.END }, null, null, null);// ,
																			// " event = ? ",
																			// null,
																			// null);
		cursor.moveToFirst();
		String CName[] = new String[cursor.getCount()];
		for (int i = 0; i < CName.length; i++) {
			long currentTime = System.currentTimeMillis();

			long eventTime = Long.parseLong(cursor.getString(0));// Returns
																	// Event
																	// Time in
																	// milliseconds
			int noOfDays = 1; // Integer.parseInt(prefs.getString("updates_notifications",
								// "null"));
			long reminderTime = eventTime - (noOfDays * 86400000);// Time in
																	// milliseconds
																	// when the
																	// alarm
																	// will
																	// shoot up
																	// & you do
																	// not need
																	// to
																	// concider
																	// month/year
																	// with this
																	// approach
																	// as time
																	// is
																	// already
																	// in
																	// milliseconds.

			// If the current time is after the event date, do not add
			// notifications
			if (currentTime < eventTime) {

				// Set alarm
				Intent myIntent = new Intent(context, AlarmReceiver.class);
				// /myIntent.putExtra("course", course);
				// /myIntent.putExtra("assignment", assignment);
				// /myIntent.putExtra("date", date);

				pendingIntent = PendingIntent.getBroadcast(context, 0,
						myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
				AlarmManager alarmManager = (AlarmManager) context
						.getSystemService(Context.ALARM_SERVICE);
				alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTime,
						pendingIntent);
			}

			cursor.moveToNext();
			// Toast.makeText(context, CName[i], Toast.LENGTH_SHORT).show();

		}
	}

	public static ArrayList<Items> readEvents(Context context) {
		ArrayList<Items> classAndEvent = new ArrayList<Items>();
		Uri uri = Uri.parse((CalendarProvider.CONTENT_URI).toString());
		Cursor cursor = context.getContentResolver().query(
				uri,
				new String[] { CalendarProvider.DESCRIPTION,
						CalendarProvider.EVENT }, null, null, null);// ,
																	// " event = ? ",
																	// null,
																	// null);
		cursor.moveToFirst();
		String CName[] = new String[cursor.getCount()];
		for (int i = 0; i < CName.length; i++) {
			classAndEvent.add(new Items(cursor.getString(0), cursor
					.getString(1)));
			CName[i] = cursor.getString(0);
			cursor.moveToNext();
		}
		return classAndEvent;
	}

	public static void deleteEvent(Context context, String className,
			String assignmentName) {
		Uri uri = Uri.parse((CalendarProvider.CONTENT_URI).toString());
		ContentResolver resolver = context.getContentResolver();
		// Cursor cursor = resolver.query(uri, new String[]
		// {CalendarProvider.DESCRIPTION, CalendarProvider.EVENT}, null, null,
		// null);
		// while(cursor.moveToNext()) {
		// long eventId =
		// cursor.getLong(cursor.getColumnIndex(CalendarProvider.EVENT));
		// resolver.delete(ContentUris.withAppendedId(uri, eventId), null,
		// null);
		// }
		// cursor.close();
		// Cursor cursor = context.getContentResolver().query(uri,
		// new String[] {CalendarProvider.DESCRIPTION, CalendarProvider.EVENT},
		// null, null, null);
		context.getContentResolver().delete(
				uri,
				CalendarProvider.DESCRIPTION + "=" + className + " AND "
						+ CalendarProvider.EVENT + "=" + assignmentName, null);
		// cursor.close();

		// Cursor cursor = context.getContentResolver().delete(uri,
		// CalendarProvider.DESCRIPTION + "=" + className + " and " +
		// CalendarProvider.EVENT + "=" + assignmentName, selectionArgs)
		// return cursor2;
	}
}
