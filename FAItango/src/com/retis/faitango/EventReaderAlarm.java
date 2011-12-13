package com.retis.faitango;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;


/* This class is responsible to set and clear the alarm 
 * for the periodic execution of the EventReader Service
 * */
public class EventReaderAlarm  {
	
	private AlarmManager alarmManager;
	private PendingIntent alarmIntent;
	private Context context;
	private long alarmOffset;
	private long alarmPeriod;
	
	public EventReaderAlarm(Context c) {
		init(c);
		readConfiguration();
	}
	
	public EventReaderAlarm(Context c, long offset, long period) {
		init(c);
		configure(offset, period);
	}
	
	public void configure(long offset, long period) {
		alarmOffset = offset;
		alarmPeriod = period;
	}
	
	public void start() {
    	alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,	
    			alarmOffset, alarmPeriod, alarmIntent);
    }
	
	public void stop() {
		alarmManager.cancel(alarmIntent);
	}
	
	private void init(Context c) {
		context = c;
		Intent i = new Intent(context, EventReader.class); 
		alarmIntent = PendingIntent.getService(context, 0, i, 0);
		alarmManager = (AlarmManager)context.getSystemService(Service.ALARM_SERVICE);
	}
	
	private void readConfiguration() {
		// chris TODO: should access a preference file to get offset and period!
		 alarmOffset = SystemClock.elapsedRealtime(); // chris TODO: understand this!
	     alarmPeriod = 5 * 1000; 
	}
}
