package com.retis.faitango;

import com.retis.faitango.DbHelper;
import com.retis.faitango.EventsAdapter;
import com.retis.faitango.EventsListener;
import com.retis.faitango.R;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

public class EventsList extends Activity {
	ListView listEvents;
	DbHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor;
	EventsAdapter adapter;
	EventsListener listener;
	
	public enum Events {
		CONCERTO, FESTA, FESTIVAL, MARATONA,
		MILONGA, SPETTACOLO, STAGE, VACANZA
	}
	
	private final String[] cities = { "Pisa", "Pisa", "Bologna", "Torino" };
	private final String[] dates = { "30/11/11", "30/11/11", "01/12/11", "02/12/11" };
	private final String[] times = { "20:30 - 01:30",
			"21:30 - 02:30", "20:30 - 00:30", "18:30 - 21:30" };
	private final Events[] eventTypes = { Events.MILONGA , Events.MILONGA,
			Events.MILONGA, Events.STAGE };
	private final String[] names = { "TanGo! Milonga del lunedì al Chattanooga, " +
			"ospiti Martin Choren e Yuka", "La Milonga del Lunes con Lunato...",
			"Milonga al Mezzo Colle di neive",
			"Corso di Tango per principianti a seguire pratica per tutti"};
		
	private void dbFill(SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		int i = 0;
		for (String city : cities) {
			values.clear();
			values.put(DbHelper.C_CITY, city);
			values.put(DbHelper.C_DATE, dates[i]);
			values.put(DbHelper.C_ID, i);
			values.put(DbHelper.C_TIME, times[i]);
			values.put(DbHelper.C_TYPE, eventTypes[i].name());
			values.put(DbHelper.C_NAME, names[i]);
			i++;
			db.insertWithOnConflict(DbHelper.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
		}
		
		
	}
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventsmain);
        
        listEvents = (ListView) findViewById(R.id.listEvents);
        
        dbHelper = new DbHelper(this);
        dbFill(dbHelper.getWritableDatabase());
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
				null, null, null, DbHelper.C_DATE + " DESC");
		startManagingCursor(cursor);
		
		// Create the adapter
	    adapter = new EventsAdapter(this, cursor);
	    listener = new EventsListener(this);
	    listEvents.setAdapter(adapter);
	    listEvents.setOnItemClickListener(listener);
		
	}
}