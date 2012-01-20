package com.retis.faitango;

import com.retis.faitango.DbHelper;
import com.retis.faitango.EventsAdapter;
import com.retis.faitango.EventsListener;
//import com.retis.faitango.DataEventParser;
import com.retis.faitango.R;

import android.app.Activity;
//import android.content.ContentValues;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;

public class EventsList extends Activity {
	private final String TAG = "EventsList";
	private ExpandableListAdapter expAdapter;
	ExpandableListView expListEvents;
	ListView listEvents;
	DbHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor;
	EventsAdapter adapter;
	EventsListener listener;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventsmain);
        expListEvents = (ExpandableListView) findViewById(R.id.expListEvents);
        dbHelper = new DbHelper(this);
        db = dbHelper.getReadableDatabase();
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		cursor = db.query(DbHelper.TABLE, null, null,
				null, DbHelper.C_DATE, null, DbHelper.C_DATE + " DESC");
		startManagingCursor(cursor);
		
		// Create the adapter
	    listener = new EventsListener(this);
	    expAdapter = new EventsTreeAdapter(cursor, this, db);
	  
	    expListEvents.setAdapter(expAdapter);
	    expListEvents.setOnChildClickListener(listener);
	}
}