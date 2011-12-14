package com.retis.faitango;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class EventsListener implements OnItemClickListener {
	Context mcontext;
	
	public EventsListener (Context context) {
		mcontext = context;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		//Toast.makeText(mcontext, "You clicked " + arg2, Toast.LENGTH_LONG).show();
		TextView info = (TextView) arg1.findViewById(R.id.textEventName);
		int visible = info.getVisibility();
		Log.d("ForecastListener", "visibility = " + visible);
		if (visible != View.VISIBLE) {
			info.setVisibility(View.VISIBLE);
		}
			
		else {
			info.setVisibility(View.GONE);
		}
	}

}
