package com.retis.faitango;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartupAlarmReceiver extends BroadcastReceiver {

	private static final String TAG = "StartupAlarmReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Refresh the alarm.");
		AlarmHelper.instance(context).refresh(context);
	}

}
