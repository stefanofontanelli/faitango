package com.retis.faitango;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;

public class ReadingReceiver extends BroadcastReceiver {

	private static final String TAG = "ReadingReceiver";
	public static final String READING_COMPLETED = "READING HAS BEEN COMPLETED.";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Intent received: " + intent.getAction());
		MainView v = (MainView) context;
		Bundle b = intent.getExtras();
		if (b != null && b.getBoolean("success"))
			v.updateEventsList();
		else
			v.notifyReadingFailure();
		Toast.makeText(context, "Unable to get event information", Toast.LENGTH_LONG).show();
	}
}
