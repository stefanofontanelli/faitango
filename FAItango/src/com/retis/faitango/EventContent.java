package com.retis.faitango;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class EventContent extends Activity {
	private static String TAG = "EventContent";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventcont);
		Bundle extras = getIntent().getExtras();
		Log.d(TAG, extras.getString("city"));
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
