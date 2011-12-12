package com.retis.faitango;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartupBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("chris", "Intent Received: " + intent.getAction());
		Log.d("chris", "Attempt to create a new Intent");
		Intent service = new Intent(context, StartupService.class);
		Log.d("chris", "Attempt to start the Service with the new Intent");
		ComponentName retv = context.startService(service);
		if (retv == null)
			Log.d("chris", "Star service has FAILED!");
		Log.d("chris", "Activation completed");
	}
}
