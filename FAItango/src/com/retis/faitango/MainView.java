package com.retis.faitango;

//import com.retis.faitango.ConfigurationView.SpinnerPeriodicEventReaderListener;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    
    private void createTemporaryLayout() {
 
        Button buttonList = (Button) findViewById(R.id.buttonOpenList);
        buttonList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Log.d("chris", "Open List CLICKED!");
                // Perform action on click
            	Context c = v.getContext();
            	Intent intent = new Intent(c, com.retis.faitango.EventsList.class);
            	c.startActivity(intent);
            }
        });
        
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
        
        Button buttonSync = (Button) findViewById(R.id.buttonSynchronize);
        buttonSync.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Log.d("chris", "Synchronized CLICKED!");
            	Context c = v.getContext();
            	Intent intent = new Intent(c, com.retis.faitango.EventReader.class);
        		EventFilter filter = PreferenceHelper.getSearchParams(c);
            	// chris FIXME: As for now I insert any time this Service is called!
            	filter.types.add(EventType.MILONGA);
            	filter.types.add(EventType.SHOW);
            	filter.dateFrom = new Date(2011, 12, 14);
            	filter.dateTo = new Date(2011, 12, 20);
        		intent.putExtra("EventFilter", filter);
            	c.startService(intent);
            }
        });
        
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