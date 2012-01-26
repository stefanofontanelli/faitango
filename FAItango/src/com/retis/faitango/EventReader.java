package com.retis.faitango;

import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/** Local (Event) DB updater <br><br>
 * 
 * The EventReader fetches the data event from the remote repository
 * and fills the local event database.
 * The class is meant to be used in two circumstances: to perform automatic synchronization;
 * to perform manual event search.
 * <br>
 * The class can be used either by a background service ({@link EventReaderService}) 
 * or by an AsyncTask ({@link EventReaderAsyncTask}).
 * In the first case the class operations are wrapped in a service which is 
 * activated by the EventReaderAlarm (see {@link EventReaderService} and {@link EventReaderAlarm}).
 * In the second case the class is wrapped by an AsyncTask which is meant to be 
 * executed by an Activity.
 * <br><br>
 * The {@link EventFilter} object (parcelable) is used to set a filter on the event fetching operation.
 * <br><br>
 * The fetching mechanism is delegated to the {@link DataEventFetcher} class (subject to the abstract factory pattern).
 * The fetched data are parsed through the {@link DataEventParser} class (subject to abstract factory pattern).
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
	//       manually set to HTTP and JSON
	private final String evFetcherType = "http";
	private final String evParserType = "json";
	private boolean isRunning = false; 
	private static final String TAG = "EventReader";
	private static EventReader singletone = null;

	private EventReader(Context c) throws Exception {
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
	
	public static EventReader instance(Context c) throws Exception {
		if (singletone == null)
			singletone = new EventReader(c);
		return singletone;
	}
	
	public boolean isExecuting() {
		synchronized (context.getApplicationContext()) {
			return isRunning;
		}
	}

	public void execute(EventFilter filter) {
		synchronized (context.getApplicationContext()) {
			Log.d(TAG, "proviamo: " + Thread.currentThread().getName());
			if (isRunning) {
				Log.d(TAG, "Already executing! EXITING!");
				return;
			} 
			isRunning = true;
		}
		// TODO: there should be some extra in the Intent defining if the either
		//       eventListReadingTask or eventDetailReadingTask has to be executed!
		// Get the EventFilter from the input Intent

		// chris TODO: delete obsolete DB entries
		// Maybe...
		// dbHelper.getWritableDatabase().delete(table, whereClause, whereArgs);
		// ... or maybe not!
		// Fetch data
		String data = evFetcher.fetchEventList(filter);
		if (data == null) {
			Log.e(TAG, "DataEventFetcher failure: got null data");
			synchronized (context.getApplicationContext()) {
				isRunning = false;
			}
			return;
		}
		// Parse data
		evParser.parseEventList(data);
		// Fill DB with parsed data
		dbFillEventList(dbHelper.getWritableDatabase(), evParser.getEvents());

		///*
    	// chris: FIXME: REMOVE THIS, JUST FOR TESTING!!!!
    	try { Log.d(TAG, "TEST-WAIT 10 secs"); Thread.sleep(10000);} catch (InterruptedException e) {}	
		// */

		Log.d(TAG, "All process is done!");
		
		synchronized (context.getApplicationContext()) {
			isRunning = false;
		}
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

	private void dbFillEventList(SQLiteDatabase db, List<DataEvent> events) {
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
			// NOTE: the SQLiteDatabase.CONFLICT_REPLACE forces the database to be refreshed with new data
			r = db.insertWithOnConflict(DbHelper.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
			if (r < 0)
				Log.w(TAG, "Error while inserting row");
			else
				Log.d(TAG, "Inserting row " + r);
		}
		db.close();
	}
}