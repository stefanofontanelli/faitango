package com.retis.faitango;

import java.text.SimpleDateFormat;

import com.retis.faitango.database.CountryProvider;
import com.retis.faitango.database.CountryTable;
import com.retis.faitango.database.EventDetailProvider;
import com.retis.faitango.database.EventProvider;
import com.retis.faitango.database.EventTable;
import com.retis.faitango.database.ProvinceProvider;
import com.retis.faitango.database.ProvinceTable;
import com.retis.faitango.database.RegionProvider;
import com.retis.faitango.database.RegionTable;
import com.retis.faitango.preference.PreferenceHelper;
import com.retis.faitango.remote.EventFilter;
import com.retis.faitango.remote.EventType;

import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;


public class MainView extends Activity {

	static final int DIALOG_ASK_SYNC = 0;
	static final int DIALOG_SYNCHRONIZING = 1;
	static final int SEARCH_DIALOG = 2;
	public static final String ALL_COUNTRIES_LABEL = "All countries";
	public static final String ALL_REGIONS_LABEL = "All regions";
	public static final String ALL_PROVINCES_LABEL = "All provinces";
	private static final String TAG = "MainView";
	private ExpandableListView eventsList;
	private Cursor cursor;
	private EventsListener listener;
	private ExpandableListAdapter listAdapter;
	private ContentResolver cr;
	private IntentFilter filter;
	private ReadingReceiver receiver;
	private boolean synchronizing;
	private EventFilter eventsListFilters;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainView onCreate.");
        setContentView(R.layout.main);
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
		Log.d(TAG, "onPause start.");
		unregisterReceiver(receiver);
		super.onPause();
		Log.d(TAG, "onPause end.");
	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy start.");
		super.onDestroy();
		Log.d(TAG, "onDestroy end.");
	}
    
    protected Dialog onCreateDialog(int id) {
    	AlertDialog.Builder builder = null;
        switch(id) {
        case DIALOG_ASK_SYNC:
    		// Build the AlertDialog to confirm or cancel synchronizing. 
        	builder = new AlertDialog.Builder(this);
        	builder.setMessage("Do you want synchronize data?");
        	builder.setCancelable(false);
        	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                MainView.this.showDialog(DIALOG_SYNCHRONIZING);
	                synchronizing = true;
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
            
        case SEARCH_DIALOG:
        	// Build the Dialog needed to specify search criterions.
            return createSearchDialog();
            
        default:
            return null;
        }
    }

    protected Dialog createSearchDialog() {        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = createSearchLayout();
        builder.setView(view);
        builder.setTitle("Search Filters");
        builder.setCancelable(false);
    	builder.setPositiveButton("Search", new SearchClickListener(this, view));
    	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int id) {
    			dialog.cancel();
    		}
    	});
        return builder.create();
    }
    
    protected View createSearchLayout() {
    	EventFilter searchPrefs = PreferenceHelper.getSearchParams(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.search, null);
        Spinner countrySpinner = (Spinner) view.findViewById(R.id.searchCountrySpinner);
        Spinner regionSpinner = (Spinner) view.findViewById(R.id.searchRegionSpinner);
        Spinner provinceSpinner = (Spinner) view.findViewById(R.id.searchProvinceSpinner);
    	LinearLayout layout = (LinearLayout) view.findViewById(R.id.searchLayout);
    	DatePicker from = (DatePicker) view.findViewById(R.id.searchFromDatePicker);
    	DatePicker to = (DatePicker) view.findViewById(R.id.searchToDatePicker);
        // Set first spinner with countries. Others spinners will be configured accordingly.
        setCountrySpinner(countrySpinner, searchPrefs.country);
        countrySpinner.setOnItemSelectedListener(new CountrySpinnerChangeListener(this, regionSpinner));
        regionSpinner.setOnItemSelectedListener(new RegionSpinnerChangeListener(this, provinceSpinner));
        
        from.updateDate(searchPrefs.dateFrom.getYear() + 1900,
        				searchPrefs.dateFrom.getMonth(),
        				searchPrefs.dateFrom.getDate());
        to.updateDate(searchPrefs.dateTo.getYear() + 1900,
        			  searchPrefs.dateTo.getMonth(),
        			  searchPrefs.dateTo.getDate());
        
        for (EventType t : EventType.values()) {
        	int rid = this.getResources().getIdentifier(t.name(), "id", this.getPackageName());
        	CheckBox cb = (CheckBox) view.findViewById(rid);
        	if (searchPrefs.types.contains(t)) {
				cb.setChecked(true);
			}
        }
        
        return view;
    }
    
    public void setCountrySpinner(Spinner spinner, String default_) {
    	int i = 1;
        int selected = 0;
    	Cursor countryCursor = cr.query(CountryProvider.CONTENT_URI, null, null, null, null);
        String[] countries = new String[countryCursor.getCount() + 1];
        countries[0] = ALL_COUNTRIES_LABEL;
        if (countryCursor.moveToFirst()) {
        	do {
        		countries[i] = countryCursor.getString(countryCursor.getColumnIndex(CountryTable.NAME));
        		if (countryCursor.getString(countryCursor.getColumnIndex(CountryTable._ID)).equals(default_)) {
        			selected = i;
        		}
        		i++;
        	} while(countryCursor.moveToNext());
        }
        ArrayAdapter countrySpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countries);
        countrySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(countrySpinnerAdapter);
        spinner.setSelection(selected);
    }
    
    public void setRegionSpinner(Spinner spinner, String country, String default_) {
        int i = 1;
        int selected = 0;
    	String where = null;
    	if (country != null && country != "") {
    		where = RegionTable.COUNTRY + "='" + country +"'";
    	}
    	Cursor regionCursor = cr.query(RegionProvider.CONTENT_URI, null, where, null, null);
        String[] regions = new String[regionCursor.getCount() + 1];
        regions[0] = ALL_REGIONS_LABEL;
        i = 1;
        selected = 0;
        if (regionCursor.moveToFirst()) {
        	do {
        		regions[i] = regionCursor.getString(regionCursor.getColumnIndex(RegionTable.NAME));
        		if (regionCursor.getString(regionCursor.getColumnIndex(RegionTable.NAME)).equals(default_)) {
        			selected = i;
        		}
        		i++;
        	} while(regionCursor.moveToNext());
        }
        Log.d(TAG, "Regions: " + regions);
        ArrayAdapter regionSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, regions);
        regionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(regionSpinnerAdapter);
        spinner.setSelection(selected);
    }
    
    public void setProvinceSpinner(Spinner spinner, String region, String default_) {
        int i = 1;
        int selected = 0;
    	String where = null;
    	if (region != null && region != "") {
    		where = ProvinceTable.REGION + "='" + region +"'";
    	}
        Cursor provinceCursor = cr.query(ProvinceProvider.CONTENT_URI, null, where, null, null);
        String[] provinces = new String[provinceCursor.getCount() + 1];
        provinces[0] = ALL_PROVINCES_LABEL;
        if (provinceCursor.moveToFirst()) {
        	do {
        		provinces[i] = provinceCursor.getString(provinceCursor.getColumnIndex(ProvinceTable.NAME));
        		if (provinceCursor.getString(provinceCursor.getColumnIndex(ProvinceTable.CODE)).equals(default_)) {
        			selected = i;
        		}
        		i++;
        	} while(provinceCursor.moveToNext());
        }
        ArrayAdapter provinceSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, provinces);
        provinceSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(provinceSpinnerAdapter);
        spinner.setSelection(selected);
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
    	Intent intent = null;
        switch (item.getItemId()) {
            case R.id.main_menu_search:
            	showDialog(SEARCH_DIALOG);
                return true;
            case R.id.main_menu_sync:
            	Log.d(TAG, "Starting ReadingService ...");
            	doSync();
                return true;
            case R.id.main_menu_setting:
				intent = new Intent(this, SettingActivity.class);
				this.startActivity(intent);
                return true;
            case R.id.main_menu_reset:
            	cr.delete(EventProvider.CONTENT_URI, null, null);
            	cr.delete(EventDetailProvider.CONTENT_URI, null, null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void updateEventsList() {
    	if (synchronizing) {
    		dismissDialog(DIALOG_SYNCHRONIZING);
    	}
    	cr = getContentResolver();
    	String[] mProjection =
    	{
   		    "DISTINCT " + EventTable.DATE,
   		    EventTable.DATE + " AS _id"
    	};
    	String where = null;
    	if (eventsListFilters != null) {
    		where = eventsListFilters.getWhereClause(cr);
    	}
		String order = EventTable.DATE + " ASC";
		cursor = cr.query(EventProvider.CONTENT_URI, mProjection, where, null, order);
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
    
    public void setSearchFilters(EventFilter f) {
    	eventsListFilters = f;
    }
}