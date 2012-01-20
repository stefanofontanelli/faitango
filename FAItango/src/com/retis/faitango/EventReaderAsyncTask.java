package com.retis.faitango;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class EventReaderAsyncTask extends AsyncTask</*Params*/EventFilter, /*Progress*/Void, /*Result*/Boolean> {

	private ProgressDialog dialog;
	private Context activityContext;
	private EventReader reader;
	private static final String TAG = "EventReaderAsyncTask";
	
	public EventReaderAsyncTask(Context contextOfActivity) {
		activityContext = contextOfActivity;
		dialog = new ProgressDialog(activityContext);
		try {
			reader = new EventReader(activityContext);
		} catch (Exception e) {
			reader = null;
			e.printStackTrace();
		}
	}
	
	@Override
	protected Boolean doInBackground(final EventFilter... args) {
		if (reader == null) {
        	Log.e(TAG, "EventReader is null. Exiting without parsing (check EventReaderAsyncTask constructor)");
			return false;
		}
		reader.execute(args[0]);
		return true;
	}
	
	@Override
	protected void onPreExecute() {
        this.dialog.setMessage("Perfavore, spetta un attimo! CRIBBIO");
        this.dialog.show();
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
