package com.retis.faitango;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainView extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("chris", "Creating MainView");
		setContentView(R.layout.main);
		createTemporaryLayout();
		doOnStartup();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("chris", "Destroying MainView");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("chris", "Resuming MainView");
	}
	
	private void doOnStartup() {
		if (PreferenceHelper.hasStartupAutoSync(this)) {
			Log.d("chris", "Starting AutoSync on application start-up");
			EventFilter filter = PreferenceHelper.getSearchParams(this);
			new EventReaderAsyncTask(this, "Synchronizing...").execute(filter);
		} 
	}

	private void createTemporaryLayout() {

		// List Button
		Button buttonList = (Button) findViewById(R.id.buttonOpenList);
		buttonList.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d("chris", "Open List CLICKED!");

				// Creating an in-line inner AsyncTask to wait on possible EventReader operations 
				(new AsyncTask<Void, Void, Boolean>() {
					// Progress dialog
					private ProgressDialog dialog = new ProgressDialog(MainView.this);

					@Override
					protected void onPreExecute() {
						dialog.setMessage("Mannagia alla miseria, il reader sta eseguendo! ASPETTA!!!");
						if (EventReader.isExecuting())
							dialog.show();
					}

					@Override
					protected Boolean doInBackground(Void... params) {
						if (!EventReader.isExecuting())
							return true;
						Log.d("chris", "EventReader is running. Waiting...");
						//long i = 0;
						while (EventReader.isExecuting()) {
							if (!dialog.isShowing()) // Give up if ProgressDialog was cancelled
								return false;
							// Wait 100ms before polling 
							try { Thread.sleep(100); } catch (InterruptedException e) {}
							//i++;
							// chris TODO: implement some MAX looping logic: if waiting too much do something!
						}
						return true;
					}

					@Override
					protected void onPostExecute(final Boolean success) {
						// If ProgrssDialog is not shown, give up 
						if (dialog.isShowing())
							dialog.dismiss();
						if (!success)
							return;
						// Perform actual actions for on click: start the list activity!
						Intent intent = new Intent(MainView.this, com.retis.faitango.EventsList.class);
						MainView.this.startActivity(intent);
					}
				}).execute();
			}
		});


		// Config Button
		Button buttonConfig = (Button) findViewById(R.id.buttonOpenConfig);
		buttonConfig.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d("chris", "Open config CLICKED!");
				// Perform action on click
				Context c = v.getContext();
				Intent intent = new Intent(c, com.retis.faitango.ConfigurationView.class);
				c.startActivity(intent);
			}
		});

		// Sync Button
		Button buttonSync = (Button) findViewById(R.id.buttonSynchronize);
		buttonSync.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d("chris", "Synchronized CLICKED!");
				EventFilter filter = PreferenceHelper.getSearchParams(v.getContext());
				new EventReaderAsyncTask(v.getContext(), null).execute(filter);
			}
		});

		// CleanDB Button
		Button buttonCleanDB = (Button) findViewById(R.id.buttonCleanDB);
		buttonCleanDB.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d("chris", "CleanDB CLICKED!");
				Context c = v.getContext();
				c.deleteDatabase(DbHelper.DB_NAME);
				Toast.makeText(c, "Internal DB just Cleaned", Toast.LENGTH_LONG).show();
			}
		});
	}
}