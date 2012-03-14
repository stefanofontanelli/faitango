package com.retis.faitango.preference;

import com.retis.faitango.AlarmHelper;
import android.content.Context;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

public class SyncTypeChangeListener implements OnPreferenceChangeListener {
	
	private static final String TAG = "SyncTypeChangeListener";
	private Context context;
	private AlarmHelper alarmHelper;
	
	public SyncTypeChangeListener(Context c) {
		super();
		context = c;
		alarmHelper = AlarmHelper.instance(context);
	}
	
	@Override
	public boolean onPreferenceChange(Preference p, Object v) {
		String value = (String) v;
		Log.d(TAG, "SyncType Preference is changed: " + value);
		if (value.contentEquals("2")) {
			alarmHelper.set(PreferenceHelper.getSyncPeriod(context));
		} else {
			alarmHelper.cancel();
		}
		return true;
	}
}
