package com.retis.faitango;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/** Update activity on events reading completed.
 * 
 * @author Christian Nastasi
 */
public class ReadingReceiver extends BroadcastReceiver {

	private static final String TAG = "ReadingReceiver";
	public static final String READING_COMPLETED = "READING HAS BEEN COMPLETED.";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Intent received: " + intent.getAction());
		// Do stuff
	}
}
