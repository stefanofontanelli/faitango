package com.retis.faitango;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

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
		if (alarmPeriod != 0)
			alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,	
					alarmOffset, alarmPeriod, alarmIntent);
		else
			alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, alarmOffset, alarmIntent);
    }
	
	public void stop() {
		alarmManager.cancel(alarmIntent);
	}
	
	private void init(Context c) {
		context = c;
		Intent passedIntent = new Intent(context, EventReader.class); 
		// Read filter from default search parameters
		EventFilter filter = PreferenceHelper.getSearchParams(context);
		passedIntent.putExtra("EventFilter", filter);
		alarmIntent = PendingIntent.getService(context, 0, passedIntent, PendingIntent.FLAG_ONE_SHOT);
		alarmManager = (AlarmManager)context.getSystemService(Service.ALARM_SERVICE);
	}
	
	private void readConfiguration() {
    	long period =  PreferenceHelper.getPeriodicAutoSyncPeriod(context);
    	alarmPeriod = period * 1000; // In milliseconds
    	alarmOffset = alarmPeriod + 10000; // Period + 10 seconds
	}
}
