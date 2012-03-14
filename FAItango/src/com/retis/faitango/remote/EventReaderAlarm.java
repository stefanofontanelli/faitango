package com.retis.faitango.remote;

import com.retis.faitango.EventReaderService;
import com.retis.faitango.preference.PreferenceHelper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

/** Activator for EventReader (singleton) <br><br>
 * 
 * This singleton class is responsible to set and clear the alarm for the (non-)periodic execution
 * of the EventReader service.
 * The {@link EventReaderAlarm} implements the automatic synchronization paradigm.
 * It supports periodic activations as well as one-shot delayed ones.
 * The class generates the Intent, to be passed to the EventReaderService, encoding an 
 * {@link EventFilter} as parcelable extra.
 * The {@link EventFilter} is formatted by the {@link PreferenceHelper#getSearchParams()} which reads the
 * default search parameters (for automatic synchronization) from the preferences. 
 * 
 * @author Christian Nastasi
 */
public class EventReaderAlarm  {

	private AlarmManager alarmManager;
	private PendingIntent alarmIntent = null;
	private Context context;
	private long alarmOffset;
	private long alarmPeriod;
	private static EventReaderAlarm singleton = null;

	/** Creates alarm from preferences
	 * 
	 * @param c The application context
	 */
	private EventReaderAlarm(Context c) {
		context = c;
		alarmManager = (AlarmManager)context.getSystemService(Service.ALARM_SERVICE);
	}

	/** Get the singleton instance
	 * 
	 * @param c The Context of the application
	 * @return	The singleton object
	 */
	public static EventReaderAlarm instance(Context c) {
		if (singleton == null)
			singleton = new EventReaderAlarm(c);
		return singleton;
	}

	/** Update alarm from parameters
	 * 
	 * @param offset The activation offset in milliseconds
	 * @param period The activation repeat times in milliseconds
	 */
	public void update(EventFilter filter, long offset, long period) {
		if (alarmIntent != null) 
			stop();
		Intent passedIntent = new Intent(context, EventReaderService.class);
		// Encode the EventFilter in the passed Intent
		passedIntent.putExtra("EventFilter", filter);
		// NOTE: The FLAG_UPDATE_CURRENT is necessary for proper forwarding of passedIntent.
		//       It shall cause the extras field (thus the EventFilter) to be updated when the
		//       PendingIntent.getSercive() is called again. See PendingIntent reference.
		alarmIntent = PendingIntent.getService(context, 0, passedIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmOffset = offset;
		alarmPeriod = period;
	}

	/** Start the alarm */
	public void start() {
		if (alarmIntent == null)
			return;
		if (alarmPeriod != 0)
			alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,	alarmOffset, alarmPeriod, alarmIntent);
		else
			alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, alarmOffset, alarmIntent);
	}

	/** Stop the alarm */
	public void stop() {
		alarmManager.cancel(alarmIntent);
		alarmIntent = null;
	}
}
