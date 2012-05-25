package com.retis.faitango.preference;

import com.retis.faitango.AlarmHelper;
import android.content.Context;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

public class SyncPeriodChangeListener implements OnPreferenceChangeListener {
	
	private static final String TAG = "SyncPeriodChangeListener";
	private Context context;
	private AlarmHelper alarmHelper;
	
	public SyncPeriodChangeListener(Context c) {
		super();
		context = c;
		alarmHelper = AlarmHelper.instance(context);
	}
	
	@Override
	public boolean onPreferenceChange(Preference p, Object v) {
		Log.d(TAG, "SyncPeriod Preference changed: " + (String) v);
		alarmHelper.cancel();
		alarmHelper.set(Long.decode((String) v));
		return true;
	}
}
