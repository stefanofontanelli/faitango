package com.retis.faitango;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/** Local (Event) DB updater
 * 
 * The EventReader service fetches the data event from the remote repository
 * and fills the local event database.
 * The service is meant to be activated in two possible ways:
 * - by the EventReaderAlarm: to perform automatic synchronization
 * - by the TODO:SearchActivity: to perform manual event search
 * The EventFilter object (parcelable), used to set an event fetch filter, 
 * is encoded as an extras in the activation Intent.     
 * 
 * The fetching mechanism is delegated to the DataEventFetcher class (subject to the abstract factory pattern).
 * The fetched data are parsed through the DataEventParser class (subject to abstract factory pattern).
 * Data are stored in the DB through the DBHelper class using an always-replace policy.
 * 
 * @author Christian Nastasi
 */
public class EventReader {
	
	private Context context;
	private DataEventFetcher evFetcher;
	private DataEventParser evParser;
	private DbHelper dbHelper;
	// NOTE: as for now, the DataEvent fetcher and parser type are 
	//       manually set respectively to HTTP and JSON
	private final String evFetcherType = "http";
	private final String evParserType = "json";
	private static final String TAG = "EventReader";
	
	public EventReader(Context c) throws Exception {
		context = c;
		// Create the proper DataEventFetcher
		evFetcher = DataEventFetcher.Factory.create(evFetcherType, context);
		if (evFetcher == null) {
			Log.e(TAG, "Error while creating DataEventFetcher");
			throw new Exception(TAG + ": Error while creating DataEventFetcher");
		}
		// Create the proper DataEventParser
		evParser = DataEventParser.Factory.create(evParserType, context);
		if (evParser == null) {
			Log.e(TAG, "Error while creating DataEventParser");
			throw new Exception(TAG + ": Error while creating DataEventParser");
		}
		// Create a DBHelper
		dbHelper = new DbHelper(c);
		// Initialize null filter
	}
	
	public synchronized void execute(EventFilter filter) {
		// TODO: there should be some extra in the Intent defining if the either
		//       eventListReadingTask or eventDetailReadingTask has to be executed!
		// Get the EventFilter from the input Intent
		
		/*
    	// chris: FIXME: REMOVE THIS, JUST FOR TESTING!!!!
    	try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
       	// chris TODO: delete obsolete DB entries
    	// Maybe...
    	// dbHelper.getWritableDatabase().delete(table, whereClause, whereArgs);
    	// ... or maybe not!
    	// Fetch data
    	String data = evFetcher.fetchEventList(filter);
    	if (data == null) {
            Log.e(TAG, "DataEventFetcher failure: got null data");
            return;
    	}
    	// Parse data
    	evParser.parseEventList(data);
    	// Fill DB with parsed data
    	dbFillEventList(dbHelper.getWritableDatabase(), evParser.getEvents());
    	Log.d(TAG, "All process is done!");
	}

	/*
	// FIXME: shall we use this? Are we going to have the Event Details?
	private Runnable eventDetailReadingTask = new Runnable() {
        public void run() { 
        	// chris TODO: this tack should get the detail of the single event.
        	//             Arguments (event id) might be passed through the activation intent
        	//String data = evFetcher.fetch(null);
        	long eventId = 0; // chris FIXME: this should be passed from the intent
        	String data = evFetcher.fetchEventDetail(eventId);
        	if (data == null) {
                Log.e(THREAD_DETAIL_TAG, "DataEventFetcher failure: got null details");
                EventReader.this.stopSelf();
                return;
        	}
        	evParser.parseEventDetail(data);
        	// chris TODO: insert in the DB (another table?) the event detail
        	//dbFillEventDetail(dbHelper.getWritableDatabase(), evParser.getEvents());
        	
        	Log.d(THREAD_DETAIL_TAG, "All process is done!");
            // Done with our work...  stop the service!
            EventReader.this.stopSelf(); 
        }
	};
	*/
	
	private void dbFillEventList(SQLiteDatabase db, ArrayList<DataEvent> events) {
		ContentValues values = new ContentValues();
		for (DataEvent ev : events) {
			values.clear();
			values.put(DbHelper.C_ID, ev.id);
			values.put(DbHelper.C_CITY, ev.city);
			values.put(DbHelper.C_DATE, ev.date.getTime());
			values.put(DbHelper.C_TIME, "21:00");
			values.put(DbHelper.C_TYPE, context.getResources().getString(ev.type.resId));
			values.put(DbHelper.C_NAME, ev.text);
			long r;
			// chris TODO: after discussing with Stefano it's likely that the conflict resolution
			//             mechanism to be used could simply be CONFLICT_REPLACE, to force update!
			r = db.insertWithOnConflict(DbHelper.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
			if (r < 0)
				Log.w(TAG, "Error while inserting row");
			else
				Log.d(TAG, "Inserting row " + r);
		}
		db.close();
	}
}