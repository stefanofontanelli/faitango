package com.retis.faitango;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
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
		
		// chris FIXME: remove manually the DB. JUST FOR TESTING!
		// chris FIXME: REMOVE THIS CODE WHEN TEST IS DONE!
		this.deleteDatabase(DbHelper.DB_NAME);
		
		// chris TODO: put in here all the code to be executed at startup (BOOT)
		//             - Read configuration files?
		//             - Plan actions to be done?
		
		// Access the (default) preference file (also used by the ConfigurationView)
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		boolean startReader = prefs.getBoolean("checkboxBgUpdater", false);
        if (startReader) {
        	Log.d("chris", "Starting EventReaderAlarm");
        	EventReaderAlarm alarm = new EventReaderAlarm(this);
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
