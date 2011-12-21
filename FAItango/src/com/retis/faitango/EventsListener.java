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
		Log.d(TAG, "groupPos = " + Integer.toString(groupPosition)+
				" childPos = " + Integer.toString(childPosition));
		// chris FIX: the startActivity was failing because the context was not the one 
		//            of an Activity (according to the exception I got).
		//            Using the context of the parent object to solve the problem. 
		//Context c = v.getContext(); 
		Context c = parent.getContext();
    	Intent intent = new Intent(c, com.retis.faitango.EventContent.class);
    	
    	/*
    	 * before starting the new intent bundle the event ID
    	 * so the new created activity can fetch info from the
    	 * database
    	 */
    	Log.d(TAG, Integer.toString(EventsTreeAdapter.childMap.get(childPosition)));
    	intent.putExtra("id", Integer.toString(EventsTreeAdapter.childMap.get(childPosition)));
    	c.startActivity(intent);
	    return true;
	}

}
