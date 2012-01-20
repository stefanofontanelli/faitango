package com.retis.faitango;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EventContent extends MapActivity {
	private static String TAG = "EventContent";
	private DbHelper dbHelper;
	private SQLiteDatabase db;
	private Cursor cursor;
	private String eventID, title, description, city;
	private Date beginTime;
	private TextView textView;
	private MapView mapView;
	private List<Overlay> mapOverlays;
	private Drawable drawable;
	private ItemizedOverlay itemizedOverlay;
	private Geocoder geocoder;
	MapController mapController;
	private double lat = 43.718326;
	private double lon = 10.424866;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventcont);
		/*
		 * retrieve the event ID and use it later (onResume)
		 * to query the DB for event info
		 */
		Bundle extras = getIntent().getExtras();
		eventID = extras.getString("id");
		Log.d(TAG, eventID);
		
        dbHelper = new DbHelper(this);
        db = dbHelper.getReadableDatabase();
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        mapOverlays = mapView.getOverlays();
		drawable = this.getResources().getDrawable(R.drawable.push_pin);
		itemizedOverlay = new ItemizedOverlay(drawable);
		
		geocoder = new Geocoder(getApplicationContext());
		
		mapController = mapView.getController();
		
		final Button button = (Button) findViewById(R.id.calbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "remainder button pressed!");

                // Statically add 3 hours, since we have no ending time 
                long start = beginTime.getTime() + ((21 * 3600000));
                long stop = start + (8 * 3600000);
                Intent intent = new Intent(Intent.ACTION_EDIT)
                        .setType("vnd.android.cursor.item/event")
                		.putExtra("title", title)
                        .putExtra("description", description)
                        .putExtra("eventLocation", city)
                		.putExtra("beginTime", start)
                		.putExtra("endTime", stop);
                startActivity(intent);
          }
        });
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		db.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		cursor = db.query(DbHelper.TABLE, null, DbHelper.C_ID + "=" + eventID,
				null, null, null, null);
		startManagingCursor(cursor);
		Log.d(TAG, Integer.toString(cursor.getCount()));
		Log.d(TAG, Integer.toString(cursor.getColumnCount()));
		Log.d(TAG, cursor.getColumnName(cursor.getColumnIndex(DbHelper.C_TYPE)));
		cursor.moveToFirst();
		
		if (cursor.getCount() > 0) {
			textView = (TextView) findViewById(R.id.textDetEventType);
			title = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.C_TYPE));
			textView.setText(title);
			
			textView = (TextView) findViewById(R.id.textDetCity);
			city = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.C_CITY));
			textView.setText(city);
			
			textView = (TextView) findViewById(R.id.textDetDate);
			beginTime = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.C_DATE)));
			String s = new SimpleDateFormat("E dd/MM/yyyy", Locale.ITALIAN).format(beginTime); 
			textView.setText(s);
			
			textView = (TextView) findViewById(R.id.textDetEventName);
			description = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.C_NAME));
			textView.setText(description);
			
			textView = (TextView) findViewById(R.id.textDetTime);
			textView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.C_TIME)));
			
			try {
				List<Address> foundAddress = geocoder.getFromLocationName(city, 1);
				Log.d(TAG, Integer.toString(foundAddress.size()));
				if (foundAddress.size() == 0) { //if no address found, display an error
		            Dialog locationError = new AlertDialog.Builder(this)
		              .setIcon(0)
		              .setTitle("Error")
		              .setPositiveButton("Ok", null)
		              .setMessage("Sorry, your address doesn't exist.")
		              .create();
		            locationError.show();
		        } else {
		        	Address x = foundAddress.get(0);
		        	lat = x.getLatitude();
		            lon = x.getLongitude();
		        }
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			GeoPoint point = new GeoPoint((int)(lat * 1E6), (int)(lon * 1E6));
			OverlayItem overlayitem = new OverlayItem(point, "", "");
			itemizedOverlay.addOverlay(overlayitem);
			mapOverlays.add(itemizedOverlay);
			mapController.animateTo(point);
	        mapController.setZoom(10); 
	        mapView.invalidate();
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
