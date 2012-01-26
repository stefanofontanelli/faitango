package com.retis.faitango;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class StartupService extends Service {

	private static final String TAG = "StartupService";
	
	@Override 
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		// chris FIXME: remove manually the DB. JUST FOR TESTING!
		// chris FIXME: REMOVE THIS CODE WHEN TEST IS DONE!
		this.deleteDatabase(DbHelper.DB_NAME);
		
		// Access the (default) preference file (also used by the ConfigurationView)
		if (PreferenceHelper.hasPeriodicAutoSync(this)) {
        	Log.d(TAG, "AutoSync active: configure and start EventReaderAlarm");
        	EventFilter filter = PreferenceHelper.getSearchParams(getApplicationContext());
        	long period =  PreferenceHelper.getPeriodicAutoSyncPeriod(getApplicationContext());
        	period = period * 1000;
        	EventReaderAlarm alarm = EventReaderAlarm.instance(getApplicationContext());
        	alarm.update(filter, period, period);
        	alarm.start();
        }
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}