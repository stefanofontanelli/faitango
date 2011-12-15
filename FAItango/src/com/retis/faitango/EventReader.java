package com.retis.faitango;

import java.util.ArrayList;
import java.util.Date;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class EventReader extends Service {
	
	private DataEventFetcher evFetcher;
	private DataEventParser evParser;
	private DbHelper dbHelper;
	private boolean properlyCreated;
	
	@Override
	public void onCreate() {
		properlyCreated = false;
		Log.d("chris", "onCreate() on EventReader");
		evFetcher = DataEventFetcher.Factory.create("http", this);
		if (evFetcher == null) {
			Log.e("chris", "Error while creating DataEventFetcher");
			return;
		}
		
		evParser = DataEventParser.Factory.create("json", this);
		if (evParser == null) {
			Log.e("chris", "Error while creating DataEventParser");
			return;
		}

		dbHelper = new DbHelper(this);
		properlyCreated = true;
	}
	
	@Override 
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Called by the StartCommand mechanism
		Log.d("chris", "onStartCommand() on EventReader");
		// chris NOTE: this is periodically called due to the timer set in 
		//             the StartupService (using AlarmManager)
		
		// chris FIXME: change this stupid work-around
		if (!properlyCreated) {
			stopSelf();
			return START_STICKY;
		}
		
		// chris NOTE: perform operations in a thread
		
		// chris TODO: there should be a parameter passed from the Intent to 
		//             tell if we have to perform either event list 
		//             or event detail fetching.	
		
		// Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.
        Thread thr = new Thread(null, eventListReadingTask, "EventReader");
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
        	// chris FIXME: As for now I insert any time this Service is called! 
        	EventFilter filter = new EventFilter();
        	filter.types.add(EventType.MILONGA);
        	filter.types.add(EventType.SHOW);
        	filter.dateFrom = new Date(2011, 12, 14);
        	filter.dateTo = new Date(2011, 12, 20);
        	filter.country = "Italia";
        	filter.region = "Toscana";
        	//String data = evFetcher.fetch(null);
        	String data = evFetcher.fetchEventList(filter);
        	if (data == null) {
                Log.d("chris", "EventList Fetcher has failed.");
                EventReader.this.stopSelf();
                return;
        	}
        	evParser.parseEventList(data);
        	dbFillEventList(dbHelper.getWritableDatabase(), evParser.getEvents());
        	
        	Log.d("chris", "READING EVENT LIST TASK DONE!");
            // Done with our work...  stop the service!
            EventReader.this.stopSelf(); 
        }
	};
	
	
	private Runnable eventDetailReadingTask = new Runnable() {
        public void run() { 
        	// chris TODO: this tack should get the detail of the single event.
        	//             Arguments (event id) might be passed throught the activation intent
        	//String data = evFetcher.fetch(null);
        	long eventId = 0; // chris FIXME: this should be passed from the intent
        	String data = evFetcher.fetchEventDetail(eventId);
        	if (data == null) {
                Log.d("chris", "Fetching event details Failure");
                EventReader.this.stopSelf();
                return;
        	}
        	evParser.parseEventDetail(data);
        	// chris TODO: insert in the DB (another table?) the event detail
        	//dbFillEventDetail(dbHelper.getWritableDatabase(), evParser.getEvents());
        	
        	Log.d("chris", "READING DETAIL TASK DONE!");
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
			r = db.insertWithOnConflict(DbHelper.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
			if (r < 0)
				Log.w("chris", "Error while inserting row ");
			else
				Log.d("chris", "Inserting Row " + r);
		}
		db.close();
	}
}