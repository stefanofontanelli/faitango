package com.retis.faitango;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/** AsyncTask-wrapper for EventReader<br><br>
 * 
 * The AsyncTask wraps the {@link EventReader} operations, displaying a ProgressDialog ("wait, please") 
 * while waiting for completion. 
 * This is meant to be executed for foreground EventReader operation, 
 * i.e. manual synchronization and search.
 * <br>
 * The {@link EventFilter} is passed as executing argument to this class.
 * 
 * @author Christian Nastasi
 */

import android.app.Activity;

public class EventReaderAsyncTask extends AsyncTask<EventFilter, Void, Boolean> {

	private Activity ctx;
	private EventReader reader;
	private static final String TAG = "EventReaderAsyncTask";

	public EventReaderAsyncTask(Activity ctx) {
		this.ctx = ctx;
		try {
			reader = new EventReader(ctx);
		} catch (Exception e) {
			reader = null;
			Log.e(TAG, "Error while creating the EventReader: ", e);
		}
	}

	@Override
	protected Boolean doInBackground(final EventFilter... args) {
		if (reader == null) {
			Log.e(TAG, "EventReader is null. Exiting without parsing (check EventReaderAsyncTask constructor)");
			return false;
		} else if (EventReader.isExecuting()) {
			// Do nothing whene EventReader executing.
			Log.d(TAG, "EventReader executing ...");
			return false;
		}
		Log.d(TAG, "Executing EventReader ...");
		reader.execute(args[0]);	
		/*
		while (EventReader.isExecuting()) {
			try { Thread.sleep(100); } catch (InterruptedException e) {}
			// chris TODO: implement some MAX looping logic: if waiting too much do something!
		}
		*/
		return true;
	}

	@Override
	protected void onPreExecute() {
		ctx.showDialog(MainView.DIALOG_SYNCHRONIZING);
	}

	@Override
	protected void onPostExecute(final Boolean success) {
		ctx.dismissDialog(MainView.DIALOG_SYNCHRONIZING);
	}
}
