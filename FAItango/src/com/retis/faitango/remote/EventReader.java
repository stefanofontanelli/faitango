package com.retis.faitango.remote;

import com.retis.faitango.AlarmHelper;
import com.retis.faitango.database.EventDetailProvider;
import com.retis.faitango.database.EventDetailTable;
import com.retis.faitango.database.EventProvider;
import com.retis.faitango.database.EventTable;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
 * activated by the EventReaderAlarm (see {@link EventReaderService} and {@link AlarmHelper}).
 * In the second case the class is wrapped by an AsyncTask which is meant to be 
 * executed by an Activity.
 * <br><br>
 * The {@link EventFilter} object (parcelable) is used to set a filter on the event fetching operation.
 * <br><br>
 * The fetching mechanism is delegated to the {@link DataEventFetcher} class (subject to the abstract factory pattern).
 * The fetched data are parsed through the {@link EventParser} class (subject to abstract factory pattern).
 * Data are stored in the DB through the DBHelper class using an always-replace policy.
 * 
 * @author Christian Nastasi
 */

public class EventReader {

	private Context context;
	private DataEventFetcher evFetcher;
	private EventParser evParser;
	private final String evFetcherType = "http";
	private final String evParserType = "json";
	private static boolean isRunning = false; 
	private static final String TAG = "EventReader";

	/** Create the EventReader
	 * 
	 * @param c			The Context of the application
	 * @throws Exception
	 */
	public EventReader(Context c) throws Exception {
		context = c;
		// Create the proper DataEventFetcher
		evFetcher = DataEventFetcher.Factory.create(evFetcherType, context);
		if (evFetcher == null) {
			Log.e(TAG, "Error while creating DataEventFetcher");
			throw new Exception(TAG + ": Error while creating DataEventFetcher");
		}
		// Create the proper DataEventParser
		evParser = EventParser.Factory.create(evParserType, context);
		if (evParser == null) {
			Log.e(TAG, "Error while creating DataEventParser");
			throw new Exception(TAG + ": Error while creating DataEventParser");
		}
	}

	public synchronized static boolean isExecuting() {
			return isRunning;
	}
	
	public void abortExecution() {
		evFetcher.stopEventFetch();
	}

	public boolean execute(EventFilter filter) {
		synchronized (this) {
			if (isRunning) {
				Log.d(TAG, "Multiple reading are not allowed... NOP!");
				return false;
			} 
			isRunning = true;
		}
		// Fetch EVENTS
		String data = evFetcher.fetchEventList(filter);
		if (data == null) {
			Log.e(TAG, "DataEventFetcher failure: got null data");
			synchronized (this) {
				isRunning = false;
			}
			return false;
		}
		// Log.d(TAG, "Received data: " + data);
		// Parse EVENTS
		evParser.parseEventList(data);
		// Insert EVENTS.
		ContentValues event = new ContentValues();
		ContentValues detail = new ContentValues();
		Log.d(TAG, "Received events: " + evParser.getEvents());
		for (DataEvent ev : evParser.getEvents()) {
			event.clear();
			detail.clear();
			event.put(EventTable._ID, ev.id);
			event.put(EventTable.NAME, ev.text);
			event.put(EventTable.CITY, ev.city);
			event.put(EventTable.DATE, ev.date.getTime());
			event.put(EventTable.TIME, "21:00");
			event.put(EventTable.TYPE, context.getResources().getString(ev.type.resId));
			handleEvent(event);
		}
		Log.d(TAG, "All fetched events were added in the content provider.");
		synchronized (this) {
			isRunning = false;
		}
		return true;
	}
	

	public boolean readDetails(long eventId) {
		String data = evFetcher.fetchEventDetail(eventId);
		if (data == null) {
			Log.e(TAG, "DataEventFetcher failure: got null data for detail of event: " + eventId);
			return false;
		}
		evParser.parseEventDetail(data);
		ContentValues detail = new ContentValues();
		DataEventDetail ed = evParser.getEventsDetails().get(0);
		detail.put(EventDetailTable.EVENT, eventId);
		detail.put(EventDetailTable.TITLE, ed.title);
		detail.put(EventDetailTable.CITY, ed.city);
		detail.put(EventDetailTable.DATE, ed.date.getTime());
		detail.put(EventDetailTable.CREATED, ed.created.getTime());
		detail.put(EventDetailTable.TIME, "21:00");
		detail.put(EventDetailTable.TYPE, context.getResources().getString(ed.type.resId));
		detail.put(EventDetailTable.EMAIL, ed.email);
		detail.put(EventDetailTable.DESCRIPTION, ed.description);
		detail.put(EventDetailTable.LINK, ed.link);
		handleEventDetail(detail);

		Log.d(TAG, "All fetched event details were added in the content provider.");
		return true;
	}
	
	public void handleEvent(ContentValues event) {
		Log.d(TAG, "Handle event: " + event);
		try {
			ContentResolver cr = context.getContentResolver();
			String where = EventTable._ID + " = " + event.getAsString("_id");
			Cursor c = cr.query(EventProvider.CONTENT_URI, null, where, null, null);
			if (c.getCount() == 0) {
				cr.insert(EventProvider.CONTENT_URI, event);
			} else if (c.moveToFirst()) {
				do {
					event.put(EventTable._ID,
							  c.getLong(c.getColumnIndex(EventTable._ID)));
					where = EventTable._ID + " = " + event.getAsString("_id");
					cr.update(EventProvider.CONTENT_URI, event, where, null);
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			Log.d(TAG, "The event " + event + " was NOT inserted/updated.");
		}
	}
	
	public void handleEventDetail(ContentValues eventDetail) {
		//Log.d(TAG, "Handle event detail: " + eventDetail);
		try {
			ContentResolver cr = context.getContentResolver();
			String where = EventDetailTable._ID + " = " + eventDetail.getAsString("event");
			Cursor c = cr.query(EventDetailProvider.CONTENT_URI, null, where, null, null);
			if (c.getCount() == 0) {
				cr.insert(EventDetailProvider.CONTENT_URI, eventDetail);
			} else if (c.moveToFirst()) {
				do {
					eventDetail.put(EventDetailTable._ID,
							  		c.getLong(c.getColumnIndex(EventDetailTable._ID)));
					where = EventDetailTable._ID + " = " + eventDetail.getAsString("id");
					cr.update(EventProvider.CONTENT_URI, eventDetail, where, null);
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			Log.d(TAG, "The event detail " + eventDetail + " was NOT inserted/updated.");
		}
	}
	
}