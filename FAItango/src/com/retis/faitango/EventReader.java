package com.retis.faitango;

import java.util.ArrayList;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
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
public class EventReader extends Service {
	
	private boolean properlyCreated;
	private DataEventFetcher evFetcher;
	private DataEventParser evParser;
	private DbHelper dbHelper;
	private EventFilter evFilter;
	// NOTE: as for now, the DataEvent fetcher and parser type are 
	//       manually set respectively to HTTP and JSON
	private final String evFetcherType = "http";
	private final String evParserType = "json";
	private static final String TAG = "EventReader";
	private static final String THREAD_DATA_TAG = "EventReaderThreadForData";
	private static final String THREAD_DETAIL_TAG = "EventReaderThreadForDetail";
	
	@Override
	public void onCreate() {
		properlyCreated = false;
		// Create the proper DataEventFetcher
		evFetcher = DataEventFetcher.Factory.create(evFetcherType, this);
		if (evFetcher == null) {
			Log.e(TAG, "Error while creating DataEventFetcher");
			return;
		}
		// Create the proper DataEventParser
		evParser = DataEventParser.Factory.create(evParserType, this);
		if (evParser == null) {
			Log.e(TAG, "Error while creating DataEventParser");
			return;
		}
		// Create a DBHelper
		dbHelper = new DbHelper(this);
		// Initialize null filter
		evFilter = null;
		properlyCreated = true;
	}
	
	@Override 
	public int onStartCommand(Intent intent, int flags, int startId) {
		// chris FIXME: change this stupid work-around ?
		if (!properlyCreated) {
			stopSelf();
			Log.e(TAG, "onStartCommand() failure: the object was not properly created by onCreate()");
			// TODO: shall we do some error notification (in the GUI)?
			return START_STICKY;
		}
		// Get the EventFilter from the input Intent
		evFilter = (EventFilter) intent.getParcelableExtra("EventFilter");
		// NOTE: perform operations in a thread (not to stall the main thread)
        Thread thr = new Thread(null, eventListReadingTask, THREAD_DATA_TAG);
        thr.start();
		
		return START_STICKY;
	}
	
	@Override 
	public IBinder onBind(Intent arg0) {
		// TODO Called by the Binding mechanism
		Log.d("chris", "onBind() on EventReader");
		return null;
	}
	
	private Runnable eventListReadingTask = new Runnable() {
        public void run() {
        	String data = evFetcher.fetchEventList(evFilter);
        	if (data == null) {
                Log.e(THREAD_DATA_TAG, "DataEventFetcher failure: got null data");
                EventReader.this.stopSelf();
                return;
        	}
        	evParser.parseEventList(data);
        	dbFillEventList(dbHelper.getWritableDatabase(), evParser.getEvents());
        	Log.d(THREAD_DATA_TAG, "All process is done!");
            EventReader.this.stopSelf(); 
        }
	};
	
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
	
	private void dbFillEventList(SQLiteDatabase db, ArrayList<DataEvent> events) {
		ContentValues values = new ContentValues();
		for (DataEvent ev : events) {
			values.clear();
			values.put(DbHelper.C_ID, ev.id);
			values.put(DbHelper.C_CITY, ev.city);
			values.put(DbHelper.C_DATE, ev.date);
			values.put(DbHelper.C_TIME, "There's no time in JSON");
			values.put(DbHelper.C_TYPE, this.getResources().getString(ev.type.resId));
			values.put(DbHelper.C_NAME, ev.text);
			long r;
			// chris TODO: after discussing with Stefano it's likely that the conflict resolution
			//             mechanism to be used could simply be CONFLICT_REPLACE, to force update!
			r = db.insertWithOnConflict(DbHelper.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
			if (r < 0)
				Log.w(THREAD_DATA_TAG, "Error while inserting row");
			else
				Log.d(THREAD_DATA_TAG, "Inserting row " + r);
		}
		db.close();
	}
}