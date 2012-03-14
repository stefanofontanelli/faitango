package com.retis.faitango;

import com.retis.faitango.database.EventProvider;
import com.retis.faitango.database.EventTable;
import com.retis.faitango.preference.PreferenceHelper;
import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;


public class MainView extends Activity implements OnItemSelectedListener {

	static final int DIALOG_ASK_SYNC = 0;
	static final int DIALOG_SYNCHRONIZING = 1;
	private static final String TAG = "MainView";
	private ExpandableListView eventsList;
	private Cursor cursor;
	private EventsListener listener;
	private ExpandableListAdapter listAdapter;
	private ContentResolver cr;
	private IntentFilter filter;
	private ReadingReceiver receiver;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainView onCreate.");
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
         * Check preference: Synchronization at Startup. 
         */
		if (PreferenceHelper.isSyncAtStartup(this)) {
			showDialog(DIALOG_ASK_SYNC);
		}
        /*
         * Fill the ListView object.
         */
		eventsList = (ExpandableListView) findViewById(R.id.mainEventsList);
		// Create and register the broadcast receiver.
		filter = new IntentFilter(ReadingService.SYNC_COMPLETED);
		receiver = new ReadingReceiver();
		cr = getContentResolver();
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(receiver, filter);
		updateEventsList();
	}
    
	@Override protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
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
	                //doSync();
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
            	Log.d(TAG, "Starting ReadingService ...");
            	doSync();
                return true;
            case R.id.main_menu_setting:
				Intent intent = new Intent(this, SettingActivity.class);
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
		String where = null;
				/*EventTable.DATE + 
					   " = '" +
					   groupCursor.getLong(groupCursor.getColumnIndexOrThrow(EventTable.DATE)) +
					   "'";*/
		cursor = cr.query(EventProvider.CONTENT_URI, null, null, null, null); //EventTable.DATE + " ASC"
		Log.d(TAG, "updateEventsList cursor: " + cursor);
		startManagingCursor(cursor);
		// Create the adapter
		if (cursor != null) {
			listener = new EventsListener(this);
			listAdapter = new EventsTreeAdapter(cursor, this);
			eventsList.setAdapter(listAdapter);
			eventsList.setOnChildClickListener(listener);
		}
    }
    
    private void doSync() {
    	Log.d(TAG, "Starting task to synchronize data ...");
    	Intent msgIntent = new Intent(this, ReadingService.class);
    	startService(msgIntent);
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