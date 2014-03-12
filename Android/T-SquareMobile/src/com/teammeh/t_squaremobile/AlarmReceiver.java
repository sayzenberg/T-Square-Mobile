package com.teammeh.t_squaremobile;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		NotificationManager myNoteMan;
		
		myNoteMan = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
		
		Notification notification = new Notification(R.drawable.ic_launcher, "Test Notification", 
				System.currentTimeMillis());
		
		
		
		
	}

}
