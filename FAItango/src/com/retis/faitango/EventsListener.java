package com.retis.faitango;

import com.retis.faitango.database.EventDetailProvider;
import com.retis.faitango.database.EventDetailTable;
import com.retis.faitango.remote.EventReader;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

public class EventsListener implements OnChildClickListener {
	private final String TAG = "EventsListener";
	Context mcontext;

	public EventsListener (Context context) {
		mcontext = context;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
			int childPosition, long id) {
		Log.d(TAG, "onChildClick():   groupPos = " + Integer.toString(groupPosition) +
				" childPos = " + Integer.toString(childPosition));
		// Get the context of the parent, which is that of an Activity as required by the Intent
		Context c = parent.getContext();
		Intent intent = new Intent(c, com.retis.faitango.EventContent.class);
		// Before starting the new intent bundle the event ID so the new created activity 
		// can fetch info from the database
		intent.putExtra("id", Integer.toString(EventsTreeAdapter.childMap.get(childPosition)));

		// Check if details are already into the DB
		if (detailsInDB(id)) {
			c.startActivity(intent);
			return true;
		}
		// if not: fetch them right away
		EventReader reader = null;
		try {
			reader = new EventReader(v.getContext());
		} catch (Exception e) {
			reader = null;
			Log.e(TAG, "Error while creating the EventReader: ", e);
			return false;
		}
		new EventDetailReaderAsyncTask(v.getContext(), reader, intent).execute(id);
		return true;
	}

	private boolean detailsInDB(long id) {
		ContentResolver cr = mcontext.getContentResolver();
		String where = EventDetailTable.EVENT + "=" + id;
		Cursor cursor = cr.query(EventDetailProvider.CONTENT_URI, null, where, null, null);
		if (cursor.getCount() <= 0) {
			cursor.close();
			return false;
		}
		cursor.close();
		return true;
	}

	private class EventDetailReaderAsyncTask extends AsyncTask<Long, Void, Boolean> {

		private ProgressDialog dialog;
		private EventReader reader;
		private Context context;
		private Intent intent;

		public EventDetailReaderAsyncTask(Context ctx, EventReader r, Intent i) {
			intent = i;
			context = ctx;
			reader = r;
			dialog = new ProgressDialog(ctx);
			dialog.setCancelable(false);
			dialog.setIndeterminate(true);
			dialog.setButton("Abort", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					reader.abortExecution();
				}
			});
		}

		protected void onPreExecute() {
			dialog.setMessage("Downloading details. Please wait ...");
			dialog.show();
		}

		protected Boolean doInBackground(Long... eventId) {
			return reader.readDetails(eventId[0]);
		}

		protected void onPostExecute(Boolean retval) {
			if (dialog.isShowing())
				dialog.dismiss();
			if (retval)
				context.startActivity(intent);
		}
	}
}
