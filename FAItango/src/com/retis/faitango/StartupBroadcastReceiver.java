package com.retis.faitango;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartupBroadcastReceiver extends BroadcastReceiver {
	
	private static final String TAG = "StartupBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Intent received: " + intent.getAction());
		// Create an intent to start the StartupService service
		Intent service = new Intent(context, StartupService.class);
		ComponentName retv = context.startService(service);
		if (retv == null)
			Log.e(TAG, "Starting StartupService has failed");
	}
}
