package com.retis.faitango;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

/** Activator for EventReader
 * 
 * Class is responsible to set and clear the alarm for the (non-)periodic execution
 * of the EventReader service.
 * The EventReaderAlarm implements the automatic synchronization paradigm.
 * It supports periodic activations as well as one-shot delayed ones.
 * The class generates the Intent, to be passed to the EventReader, encoding an 
 * EventFilter as parcelable extra.
 * The EventFilter is formatted by the PreferenceHelper.getSearchParams() which reads the
 * default search parameters (for automatic synchronization) from the preferences. 
 * 
 * @author Christian Nastasi
 */
public class EventReaderAlarm  {
	
	private AlarmManager alarmManager;
	private PendingIntent alarmIntent;
	private Context context;
	private long alarmOffset;
	private long alarmPeriod;
	
	/** Creates alarm from preferences
	 * 
	 * @param c The application context
	 */
	public EventReaderAlarm(Context c) {
		init(c);
		readConfiguration();
	}
	
	/** Creates alarm from parameters
	 * 
	 * @param c      The application context
	 * @param offset The activation offset in milliseconds
	 * @param period The activation repeat times in milliseconds
	 */
	public EventReaderAlarm(Context c, long offset, long period) {
		init(c);
		alarmOffset = offset;
		alarmPeriod = period;
	}
	
	/** Start the alarm
	 */
	public void start() {
		if (alarmPeriod != 0)
			alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,	
					alarmOffset, alarmPeriod, alarmIntent);
		else
			alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, alarmOffset, alarmIntent);
    }
	
	/** Stop the alarm
	 */
	public void stop() {
		alarmManager.cancel(alarmIntent);
	}
	
	private void init(Context c) {
		context = c;
		Intent passedIntent = new Intent(context, EventReaderService.class); 
		// Read filter from default search parameters
		EventFilter filter = PreferenceHelper.getSearchParams(context);
		// Encode the EventFilter in the passed Intent
		passedIntent.putExtra("EventFilter", filter);
		// NOTE: The FLAG_UPDATE_CURRENT is necessary for proper forwarding of passedIntent.
		//       It shall cause the extras field (thus the EventFilter) to be updated when the
		//       PendingIntent.getSercive() is called again. See PendingIntent reference.
		alarmIntent = PendingIntent.getService(context, 0, passedIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager = (AlarmManager)context.getSystemService(Service.ALARM_SERVICE);
	}
	
	private void readConfiguration() {
		// Get automatic synchronization periods (repeat time) from preferences.
    	long period =  PreferenceHelper.getPeriodicAutoSyncPeriod(context);
    	alarmPeriod = period * 1000; // In milliseconds
    	alarmOffset = alarmPeriod + 1000; // Period + 1 second
	}
}
