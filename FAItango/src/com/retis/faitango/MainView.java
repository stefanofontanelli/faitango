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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;


public class MainView extends Activity implements OnItemSelectedListener {

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
        spinner.setOnItemSelectedListener(this);
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
				Intent intent = new Intent(this, GlobalPreferenceActivity.class);
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
    
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Toast.makeText(parent.getContext(), "The planet is " +
            parent.getItemAtPosition(pos).toString() + " - " + view.getResources().getResourceName(R.array.mainSpinnerItems),
            Toast.LENGTH_LONG).show();
      }

      public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing.
      }
}