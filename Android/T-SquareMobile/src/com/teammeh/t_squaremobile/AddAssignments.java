package com.teammeh.t_squaremobile;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;

import com.tyczj.extendedcalendarview.CalendarProvider;
import com.tyczj.extendedcalendarview.Event;

public class AddAssignments {
	
	private static PendingIntent pendingIntent;
	
	public static ContentValues addToCal(String course, String assignment, int startYear, int startMonth, int startDay){
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

		//Uri uri = getContentResolver().insert(CalendarProvider.CONTENT_URI, values);
		return values;
	}
	
	public static void setNotification(Context context, String course, String assignment, int year, int month, int day){
		
		//if (Integer.parseInt(prefs.getString("updates_notifications", "null")) != 0){
		//String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
		//String date = months[month] + " " + day + ", " + year;
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		long currentTime = System.currentTimeMillis();
		
		long eventTime=calendar.getTimeInMillis();//Returns Event Time in milliseconds
		int noOfDays=1; //Integer.parseInt(prefs.getString("updates_notifications", "null"));
		long reminderTime=eventTime-(noOfDays*86400000);//Time in milliseconds when the alarm will shoot up & you do not need to concider month/year with this approach as time is already in milliseconds.
	
		// If the current time is after the event date, do not add notifications
		if (currentTime < eventTime){

		//Set alarm
		Intent myIntent = new Intent(context, AlarmReceiver.class);
		///myIntent.putExtra("course", course);
		///myIntent.putExtra("assignment", assignment);
		///myIntent.putExtra("date", date);
		
		pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent,PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
		}
		//}
	}
}
