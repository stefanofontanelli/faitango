package com.retis.faitango;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class StartupService extends Service {

    private PendingIntent alarmSender;
	
	@Override
	public void onCreate() {
		// TODO: Called when creating the Service
		Log.d("chris", "onCreate() on Startup Service");
	}
	
	@Override 
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Called by the StartCommand mechanism
		Log.d("chris", "onStartCommand() on Startup Service");
		
		// chris TODO: put in here all the code to be executed at startup (BOOT)
		//             - Read configuration files?
		//             - Plan actions to be done?
		
		// chris NOTE: now I am using this to setup the periodic alarm to 
		//             read from Internet the messages (as an email reader)
		//             We might read a config file to know the frequency
		
		// Create an IntentSender that will launch our service, to be scheduled
        // with the alarm manager.
		Intent i = new Intent(StartupService.this, EventReader.class); 
        alarmSender = PendingIntent.getService(StartupService.this, 0, i, 0);
        
        long firstTime = SystemClock.elapsedRealtime(); // chris TODO: understand this!
        long period = 1 * 1000; // chris TODO: hard-code: read from a config file!
        
        // Schedule the alarm
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        //am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, period, alarmSender);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 0, alarmSender);
        //am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, alarmSender);
        
        // chris TODO: how do we stop this periodic timer? There should be an activity that
        //             kills any access to internet!
        
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d("chris", "onBind() on Startup Service");
		// TODO Auto-generated method stub
		return null;
	}

}
