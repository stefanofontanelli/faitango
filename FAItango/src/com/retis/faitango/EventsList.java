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
import android.provider.Contacts.People;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

public class EventsList extends Activity {
	private int mGroupIdColumnIndex;
	private ExpandableListAdapter expAdapter;
	ExpandableListView expListEvents;
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
        
        expListEvents = (ExpandableListView) findViewById(R.id.expListEvents);
        
        dbHelper = new DbHelper(this);
        dbFill(dbHelper.getWritableDatabase());
        db = dbHelper.getReadableDatabase();
        
        /*
        String jsonTest = "[{\"id\":89174,\"tx\":\"Milonga Linda Sp\u00E9cial Anniversaire\",\"dt\":" +
                            "\"mar 06\\/12\\/2011\",\"citta\":\"Francia - Cagnes sur Mer\",\"type\"" + 
        		            ":\"Milonga\",\"af\":null}]";
        
        DataEventParser QUI = null;
        QUI = DataEventParser.Factory.create("json");		
        if (QUI == null) {
        	Toast.makeText(this, "Creato QUI, ma è vuoto!", Toast.LENGTH_LONG).show();
        	return;
        }
        Toast.makeText(this, QUI.getTestString(), Toast.LENGTH_LONG).show();   
        QUI.parse(jsonTest);   
        Toast.makeText(this, QUI.getTestString(), Toast.LENGTH_LONG).show();
        */    
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
	    //adapter = new EventsAdapter(this, cursor);
	    //listener = new EventsListener(this);
	    expAdapter = new EventsTreeAdapter(cursor, this, db);
	    //listEvents.setAdapter(adapter);
	    //listEvents.setOnItemClickListener(listener);
	    expListEvents.setAdapter(expAdapter);
		
	}
}