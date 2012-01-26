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
public class EventReaderAsyncTask extends AsyncTask<EventFilter, Void, Boolean> {

	private ProgressDialog dialog;
	private Context activityContext;
	private EventReader reader;
	private String waitMessage;
	private static final String TAG = "EventReaderAsyncTask";

	public EventReaderAsyncTask(Context contextOfActivity, String displayedWaitMessage) {
		activityContext = contextOfActivity;
		if (displayedWaitMessage != null)
			waitMessage = displayedWaitMessage;
		else
			waitMessage = "Perfavore, spetta un attimo! CRIBBIO";
		dialog = new ProgressDialog(activityContext);
		try {
			reader = new EventReader(activityContext);
		} catch (Exception e) {
			reader = null;
			Log.e(TAG, "Error while creating the EventReader: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	protected Boolean doInBackground(final EventFilter... args) {
		if (reader == null) {
			Log.e(TAG, "EventReader is null. Exiting without parsing (check EventReaderAsyncTask constructor)");
			return false;
		}
		if (EventReader.isExecuting())
			Log.d(TAG, "EventReader already exetuing (probably in Service). Waiting");
		while (EventReader.isExecuting()) {
			try { Thread.sleep(100); } catch (InterruptedException e) {}
			// chris TODO: implement some MAX looping logic: if waiting too much do something!
		}
		Log.d(TAG, "Exetuting EventReader from ASYNCTASK");
		reader.execute(args[0]);
		return true;
	}

	@Override
	protected void onPreExecute() {
		dialog.setMessage(waitMessage);
		dialog.show();
	}

	@Override
	protected void onPostExecute(final Boolean success) {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}
}
