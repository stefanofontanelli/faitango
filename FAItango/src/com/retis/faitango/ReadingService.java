package com.retis.faitango;

import com.retis.faitango.preference.PreferenceHelper;
import com.retis.faitango.remote.EventReader;
import com.retis.faitango.AlarmHelper;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


public class ReadingService extends IntentService {

	private static final String TAG = "ReadingService";
	private EventReader reader;
	private NotificationManager notificationManager;
	private Notification readingNotification;
	private Intent notificationIntent;
	private PendingIntent contentIntent;
	private Intent broadcastIntent;
	private AlarmHelper alarmHelper;
	public static final int NOTIFICATION_ID = 1;
	public static final String SYNC_COMPLETED = "com.retis.faitango.SYNC_COMPLETED";
	private boolean abortReceived;
	
	public ReadingService() {
		super("ReadingService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate ...");
		
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID);
		readingNotification = new Notification(R.drawable.ic_launcher,
											   "FAITango events has been updated.",
											   System.currentTimeMillis());
		notificationIntent = new Intent(this, MainView.class);
		contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		readingNotification.setLatestEventInfo(this.getApplicationContext(),
											   "FAITango events has been updated",
											   "Open FAITango to see them!",
											   contentIntent);
		readingNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		broadcastIntent = new Intent(SYNC_COMPLETED);
		alarmHelper = AlarmHelper.instance(this);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		Bundle b = intent.getExtras();
		if (b != null && b.getBoolean("stop")) {
			if (reader != null)
					reader.abortExecution();
			abortReceived = true;
		} else {
			try {
				reader = new EventReader(getApplicationContext());
			} catch (Exception e) {
				reader = null;
				Log.e(TAG, "Error while creating the EventReader: ", e);
			}
			abortReceived = false;
			alarmHelper.refresh(this);
		}
	    return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "Handling intent ...");
		if (abortReceived) {
			stopSelf();
			return;
		}
		if (reader != null) {
			if (reader.execute(PreferenceHelper.getSearchParams(this))) {
				notificationManager.notify(NOTIFICATION_ID, readingNotification);
				broadcastIntent.putExtra("success", true);
			} else {
				broadcastIntent.putExtra("success", false);
			}
			sendBroadcast(broadcastIntent);
		} else {
			Log.d(TAG, "EventReader is null.");
		}
	}
}