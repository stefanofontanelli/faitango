package com.retis.faitango;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class EventReaderService extends Service {

	private static boolean serviceRunning = false;
	private static final String TAG = "EventReaderService";
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// LOCK service: if there is an already existing instance, abort the new one. 
		synchronized(this) {
			if (serviceRunning) {
				Log.d(TAG, "The reading service is already running. Stopping this instance.");
				stopSelf();
				return START_STICKY;
			}
			serviceRunning = true;
		}
		EventReader reader = null;
		try {
			reader = new EventReader(this);
		} catch (Exception e) {
			Log.e(TAG, "onStartCommand() failure: the object was not properly created by onCreate()");
			stopSelf();
			//e.printStackTrace();
			// TODO: shall we do some error notification (in the GUI)?
			return START_STICKY;
		}
		// Execute actual service code
		reader.execute((EventFilter) intent.getParcelableExtra("EventFilter"));
		// UNLOCK service
		synchronized(this) {
			serviceRunning = false;
		}
		stopSelf();
		return START_STICKY;
	}
}
