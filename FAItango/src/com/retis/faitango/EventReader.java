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
		
		evParser = DataEventParser.Factory.create("json");
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
		
		// Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.
        Thread thr = new Thread(null, readingTask, "EventReader");
        thr.start();
		
		return START_STICKY;
	}
	
	@Override 
	public IBinder onBind(Intent arg0) {
		// TODO Called by the Binding mechanism
		Log.d("chris", "onBind() on EventReader");
		return null;
	}
	
	private Runnable readingTask = new Runnable() {
        public void run() {
    		// chris TODO: - Access the network (retrieve the event data from web)
    		//             - Parse the event data (JSON supported)
    		//             - Add parsed data in the DB
        	
        	// chris FIXME: Policies and Checks need to be implemented:
        	//              - Do I fetch all event data every time?
        	//              - Shall I check if the entry is already in the DB?
       		//              - Shall I remove entries from the DB?
        	
        	// chris FIXME: As for now I insert any time this Service is called! 
        	EventFilter filter = new EventFilter();
        	filter.types.add(DataEvent.Types.MILONGA);
        	filter.types.add(DataEvent.Types.SHOW);
        	filter.dateFrom = new Date(2011, 12, 14);
        	filter.dateTo = new Date(2011, 12, 20);
        	filter.country = "Italia";
        	filter.region = "Toscana";
        	//String data = evFetcher.fetch(null);
        	String data = evFetcher.fetch(filter);
        	if (data == null) {
                Log.d("chris", "DataEventFetcher has failed.");
        		// Done with our work...  stop the service!
                EventReader.this.stopSelf(); // chris TODO: do we need this?
                return;
        	}
        	evParser.parse(data);
        	dbFill(dbHelper.getWritableDatabase(), evParser.getEvents());
        	
        	Log.d("chris", "READING TASK DONE!");
            // Done with our work...  stop the service!
            EventReader.this.stopSelf(); // chris TODO: do we need this?
        }
	};
	
	private void dbFill(SQLiteDatabase db, ArrayList<DataEvent> events) {
		ContentValues values = new ContentValues();
		for (DataEvent ev : events) {
			values.clear();
			values.put(DbHelper.C_ID, ev.id);
			values.put(DbHelper.C_CITY, ev.city);
			values.put(DbHelper.C_DATE, ev.date);
			values.put(DbHelper.C_TIME, ev.date);
			values.put(DbHelper.C_TYPE, ev.type);
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