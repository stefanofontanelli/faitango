package com.retis.faitango;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.preference.PreferenceManager;


public class StartupService extends Service {

	@Override
	public void onCreate() {
		Log.d("chris", "onCreate() on Startup Service");
	}
	
	@Override 
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("chris", "onStartCommand() on Startup Service");
		
		// chris TODO: put in here all the code to be executed at startup (BOOT)
		//             - Read configuration files?
		//             - Plan actions to be done?
		
		// Access the (default) preference file (also used by the ConfigurationView)
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		boolean startReader = prefs.getBoolean("checkboxBgUpdater", true);
        if(startReader) {
        	long period =  Long.decode(prefs.getString("listBgUpdaterPeriods", "3"));
        	period = period * 1000; // In milliseconds
        	long firstTime = SystemClock.elapsedRealtime(); // chris TODO: understand this!
        	Log.d("chris", "Starting EventReaderAlarm with " + period);
        	EventReaderAlarm alarm = new EventReaderAlarm(this, firstTime, period);
        	alarm.start();
        }

		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d("chris", "onBind() on Startup Service");
		// TODO Auto-generated method stub
		return null;
	}

}
