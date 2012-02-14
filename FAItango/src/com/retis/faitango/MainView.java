package com.retis.faitango;

import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;


public class MainView extends Activity {

	static final int DIALOG_ASK_SYNC = 0;
	static final int DIALOG_SYNCHRONIZING = 1;
	private static final String TAG = "MainView";
	DbHelper dbHelper;
	SQLiteDatabase db;
	ExpandableListView eventsList;
	Cursor cursor;
	EventsListener listener;
	ExpandableListAdapter listAdapter;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        /*
         * Fill the Spinner object.
         */
        Spinner spinner = (Spinner) findViewById(R.id.mainSpinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
        		R.array.mainSpinnerItems,
        		android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new MainSpinnerItemSelectedListener());
        /*
         * Check for Startup Auto Synch. 
         */
		if (PreferenceHelper.hasStartupAutoSync(this)) {
			showDialog(DIALOG_ASK_SYNC);
		}
        /*
         * Fill the ListView object.
         */
		eventsList = (ExpandableListView) findViewById(R.id.mainEventsList);
		dbHelper = new DbHelper(this);
		db = dbHelper.getReadableDatabase();
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		updateEventsList();
	}
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}
    
    protected Dialog onCreateDialog(int id) {
        switch(id) {
        case DIALOG_ASK_SYNC:
    		// Build the AlertDialog to confirm or cancel synchronizing. 
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("Do you want synchronize data?");
        	builder.setCancelable(false);
        	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                MainView.this.showDialog(DIALOG_SYNCHRONIZING);
	                doSync();
	           }
        	});
        	builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        			dialog.cancel();
        		}
        	});
        	return builder.create();
        case DIALOG_SYNCHRONIZING:
        	// Build the ProgressDialog to feedback about status.
            ProgressDialog dialog = new ProgressDialog(MainView.this);
            dialog.setMessage("Downloading. Please wait ...");
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            return dialog;
        default:
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	Log.d(TAG, "onCreateOptionsMenu!");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.main_menu_search:
            	Toast.makeText(this, "Search!", Toast.LENGTH_LONG).show();
                return true;
            case R.id.main_menu_sync:
                doSync();
                return true;
            case R.id.main_menu_setting:
				Intent intent = new Intent(this, com.retis.faitango.ConfigurationView.class);
				this.startActivity(intent);
                return true;
            case R.id.main_menu_help:
            	Toast.makeText(this, "Help Me!", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void updateEventsList() {
		cursor = db.query(DbHelper.TABLE,
				 null,
				 null,
				 null,
				 DbHelper.C_DATE,
				 null,
				 DbHelper.C_DATE + " ASC");
		startManagingCursor(cursor);
		// Create the adapter
		listener = new EventsListener(this);
		listAdapter = new EventsTreeAdapter(cursor, this, db);
		eventsList.setAdapter(listAdapter);
		eventsList.setOnChildClickListener(listener);
    }
    
    private void doSync() {
    	Log.d(TAG, "Starting task to synchronize data ...");
		EventFilter filter = PreferenceHelper.getSearchParams(this);
		new EventReaderAsyncTask(this).execute(filter);
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
		/*
		Button buttonSync = (Button) findViewById(R.id.buttonSynchronize);
		buttonSync.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d("chris", "Synchronized CLICKED!");
				EventFilter filter = PreferenceHelper.getSearchParams(v.getContext());
				new EventReaderAsyncTask(v.getContext()).execute(filter);
			}
		});*/

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