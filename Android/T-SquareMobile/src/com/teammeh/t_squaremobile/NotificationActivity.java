package com.teammeh.t_squaremobile;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.view.Menu;

public class NotificationActivity extends Service {

	private NotificationManager mManager;
	 
    @Override
    public IBinder onBind(Intent arg0)
    {
       // TODO Auto-generated method stub
        return null;
    }
 
    @Override
    public void onCreate() 
    {
       // TODO Auto-generated method stub  
       super.onCreate();
    }
 
   @SuppressWarnings("static-access")
   @Override
   public void onStart(Intent intent, int startId)
   {
       super.onStart(intent, startId);
      
       mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
       Intent intent1 = new Intent(this.getApplicationContext(),LoginActivity.class);
     
       ///String courseName = intent1.getStringExtra("course");
       ///String assignmentName = intent1.getStringExtra("assignment");
       ///String dateName = intent1.getStringExtra("date");
       ///String description = assignmentName + " for " + courseName + " is due on " + dateName;
       
       Notification notification = new Notification(R.drawable.ic_launcher, "Assignment notice from BeeSquared", System.currentTimeMillis());
       intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
 
       PendingIntent pendingNotificationIntent = PendingIntent.getActivity( this.getApplicationContext(),0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);
       notification.flags |= Notification.FLAG_AUTO_CANCEL;
       notification.setLatestEventInfo(this.getApplicationContext(), "BeeSquared", "You have an assignment due tomorrow!", pendingNotificationIntent);
 
       mManager.notify(0, notification);
    }
 
    @Override
    public void onDestroy() 
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
