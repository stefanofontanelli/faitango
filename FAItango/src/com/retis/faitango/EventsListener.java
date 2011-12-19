package com.retis.faitango;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

public class EventsListener implements OnChildClickListener {
	private final String TAG = "EventsListener";
	Context mcontext;
	
	public EventsListener (Context context) {
		mcontext = context;
	}
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
	        int childPosition, long id) {
	    // use groupPosition and childPosition to locate the current item in the adapter
		Log.d(TAG, "groupPos = " + Integer.toString(groupPosition)+
				" childPos = " + Integer.toString(childPosition));
		Context c = v.getContext();
    	Intent intent = new Intent(c, com.retis.faitango.EventContent.class);
    	intent.putExtra("id", Long.toString(id));
    	c.startActivity(intent);
	    return true;
	}

}
