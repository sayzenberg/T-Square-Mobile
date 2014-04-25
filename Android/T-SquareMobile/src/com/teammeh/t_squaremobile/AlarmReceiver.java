package com.teammeh.t_squaremobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// AlarmReceiver is a BroadcastReceiver is receiving an Intent broadcast and will start the notification activity
public class AlarmReceiver extends BroadcastReceiver {
    
  @Override
  public void onReceive(Context context, Intent intent)
  {
     Intent service1 = new Intent(context, NotificationActivity.class);
     context.startService(service1);
      
  }   
}
