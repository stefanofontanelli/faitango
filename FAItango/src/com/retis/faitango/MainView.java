package com.retis.faitango;

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
        		EventFilter filter = PreferenceHelper.getSearchParams(v.getContext());
        		new EventReaderAsyncTask(v.getContext()).execute(filter);
        		
            	// chris TODO: there should be a "Please, wait" dialog (maybe including a "cancel" button)
            	//             which is displayed while performing the Sync operation.
            	//             DO IT!
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