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
       Intent intent1 = new Intent(this.getApplicationContext(), LoginActivity.class);
     
       //Sets the view of the notification in status bar
       Notification notification = new Notification(R.drawable.ic_launcher, "Assignment notice from T-Square Mobile", System.currentTimeMillis());
       // If the activity is already open, notification will not initialize new activity
       intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
 
       PendingIntent pendingNotificationIntent = PendingIntent.getActivity( this.getApplicationContext(),0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);
       // Lets notification be removed when clicked
       notification.flags |= Notification.FLAG_AUTO_CANCEL;
       // Displays the content of the notification
       notification.setLatestEventInfo(this.getApplicationContext(), "T-Square Mobile", "You have an assignment due tomorrow!", pendingNotificationIntent);
 
       //posts notification in status bar
       mManager.notify((int)(Math.random()*1000), notification);
    }
 
    @Override
    public void onDestroy() 
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
