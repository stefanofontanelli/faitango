package com.retis.faitango;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class ConfigurationView extends Activity {
	
	private Spinner bgUpdaterSpinner;
	private Button bgUpdaterToggleButton;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("chris", "Creating CongigurationView");
        
        setContentView(R.layout.configuration);
        createBgUpdaterLayout();
    }
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("chris", "Destroying CongigurationView");
	}
    
    @Override
	protected void onResume() {
		super.onResume();
		Log.d("chris", "Resuming CongigurationView");
    }
    
    /*****************************************/
    /*** Background Updater configuration ***/
    /*****************************************/
    
    public class SpinnerPeriodicEventReaderListener implements OnItemSelectedListener {

    	@Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    		String s = "Period selected: " + parent.getItemAtPosition(pos).toString();
    		Toast.makeText(parent.getContext(), s, Toast.LENGTH_LONG).show();
        }

    	@Override
        public void onNothingSelected(AdapterView<?> parent) {
    		Toast.makeText(parent.getContext(), "Nothing selected!", Toast.LENGTH_LONG).show();
        }
    }
    
    private void createBgUpdaterLayout() {
    	// Spinner configuration
        bgUpdaterSpinner = (Spinner) findViewById(R.id.spinnerPeriodicEventReader);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.bgUpdaterPeriods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bgUpdaterSpinner.setAdapter(adapter);
        bgUpdaterSpinner.setOnItemSelectedListener(new SpinnerPeriodicEventReaderListener());
        // On-Off button configuration
        bgUpdaterToggleButton = (Button) findViewById(R.id.togglePeriodicEventReader);
        bgUpdaterToggleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	String s = "Toggle Selected: " + bgUpdaterToggleButton.getText().toString();
            	Toast.makeText(v.getContext(), s, Toast.LENGTH_LONG).show();
            }
        });
    }
}
