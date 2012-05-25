package com.retis.faitango;

import com.retis.faitango.preference.PreferenceHelper;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class AlarmHelper  {

	private static final String TAG = "AlarmHelper";
	private AlarmManager alarms;
	private PendingIntent alarmIntent;
	private Context context;
	private static AlarmHelper singleton = null;
	public static final String ALARM_ACTION = "Synchronize!";

	/** Creates alarm from preferences
	 * 
	 * @param c The application context
	 */
	private AlarmHelper(Context c) {
		context = c;
		alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmIntent = null;
	}

	/** Get the singleton instance
	 * 
	 * @param c The Context of the application
	 * @return	The singleton object
	 */
	public static AlarmHelper instance(Context c) {
		if (singleton == null) {
			singleton = new AlarmHelper(c);
		}
		return singleton;
	}

	public void set(long period) {
		Log.d(TAG, "Set alarm");
		cancel();
        // Encode the EventFilter in the passed Intent
		Intent passedIntent = new Intent(context, ReadingService.class);
        passedIntent.putExtra("filter", PreferenceHelper.getSearchParams(context));
        passedIntent.putExtra("background", true);
        // NOTE: The FLAG_UPDATE_CURRENT is necessary for proper forwarding of passedIntent.
        //       It shall cause the extras field (thus the EventFilter) to be updated when the
        //       PendingIntent.getSercive() is called again. See PendingIntent reference.
        alarmIntent = PendingIntent.getService(context, 0, passedIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarms.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				   				   SystemClock.elapsedRealtime() + period,
				   				   period,
				   				   alarmIntent);
	}
	
	public void refresh(Context context) {
		Log.d(TAG, "Refresh alarm");
		if (PreferenceHelper.isSyncPeriodic(context)) {
			set(PreferenceHelper.getSyncPeriod(context));
		}
	}
	
	public void cancel() {
		Log.d(TAG, "Cancel alarm");
		if (alarmIntent != null)
			alarms.cancel(alarmIntent);
		alarmIntent = null;
	}
}
