package com.retis.faitango;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.retis.faitango.database.EventDetailProvider;
import com.retis.faitango.database.EventDetailTable;
import com.retis.faitango.database.EventTable;
import com.facebook.android.*;
import com.facebook.android.Facebook.*;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class EventContent extends MapActivity {
	private static String TAG = "EventContent";
	private Cursor cursor;
	private String eventID, title, description, city;
	private Date beginTime;
	private TextView textView;
	private MapView mapView;
	private List<Overlay> mapOverlays;
	private Drawable drawable;
	private ItemizedOverlay itemizedOverlay;
	private Geocoder geocoder;
	private MapController mapController;
	private double lat = 43.718326;
	private double lon = 10.424866;
	private Facebook facebook = new Facebook("151539328293835");
	private SharedPreferences mPrefs;
	private ContentResolver cr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventcont);
		// Retrieve the event ID and use it later (onResume) to query the DB for event info
		Bundle extras = getIntent().getExtras();
		eventID = extras.getString("id");
		Log.d(TAG, "onCreate(): event ID = " + eventID);

		cr = getContentResolver();
		
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		mapOverlays = mapView.getOverlays();
		drawable = this.getResources().getDrawable(R.drawable.push_pin);
		itemizedOverlay = new ItemizedOverlay(drawable);

		geocoder = new Geocoder(getApplicationContext());

		mapController = mapView.getController();

		final Button calButton = (Button) findViewById(R.id.calbutton);
		calButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "Remainder button pressed!");

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

		final Button faceButton = (Button) findViewById(R.id.facebookButton);
		faceButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "facebook button pressed!");

				facebookAuth();

				//post on user's wall.
				/*
				 * FIXME this should be correct (according to the
				 * documentation).. anyway, the post dialog is
				 * correctly displayed, and the posting works..
				 * no luck with params :-|
				 */

				Bundle params = new Bundle();
				params.putString("message", "messaggio");
				params.putString("name", "nome");
				params.putString("caption", "caption");
				//params.putString("link", "http://www.londatiga.net");
				params.putString("description", "Dexter, seven years old dachshund who loves to catch cats, eat carrot and krupuk");
				//params.putString("picture", "http://twitpic.com/show/thumb/6hqd44");

				facebook.dialog(EventContent.this, "feed", params, new UpdateStatusListener());
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();

		facebook.extendAccessTokenIfNeeded(this, null);
		String where = EventDetailTable._ID + "=" + eventID;
		cursor = cr.query(EventDetailProvider.CONTENT_URI, null, where, null, EventTable.DATE + " ASC");
		startManagingCursor(cursor);
		Log.d(TAG, Integer.toString(cursor.getCount()));
		Log.d(TAG, Integer.toString(cursor.getColumnCount()));
		Log.d(TAG, cursor.getColumnName(cursor.getColumnIndex(EventDetailTable.TYPE)));
		cursor.moveToFirst();

		if (cursor.getCount() > 0) {
			textView = (TextView) findViewById(R.id.textDetEventType);
			title = cursor.getString(cursor.getColumnIndexOrThrow(EventDetailTable.TYPE));
			textView.setText(title);

			textView = (TextView) findViewById(R.id.textDetCity);
			city = cursor.getString(cursor.getColumnIndexOrThrow(EventDetailTable.CITY));
			textView.setText(city);

			textView = (TextView) findViewById(R.id.textDetDate);
			beginTime = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(EventDetailTable.DATE)));
			String s = new SimpleDateFormat("E dd/MM/yyyy", Locale.ITALIAN).format(beginTime); 
			textView.setText(s);

			textView = (TextView) findViewById(R.id.textDetEventName);
			description = cursor.getString(cursor.getColumnIndexOrThrow(EventDetailTable.DESCRIPTION));
			textView.setText(description);

			textView = (TextView) findViewById(R.id.textDetTime);
			textView.setText(cursor.getString(cursor.getColumnIndexOrThrow(EventDetailTable.TIME)));

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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		facebook.authorizeCallback(requestCode, resultCode, data);
	}

	private void facebookAuth() {
		// Get existing access_token if any
		mPrefs = getPreferences(MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);
		if(access_token != null) {
			facebook.setAccessToken(access_token);
		}
		if(expires != 0) {
			facebook.setAccessExpires(expires);
		}

		// Only call authorize if the access_token has expired.
		if(!facebook.isSessionValid()) {
			/*
			 * The user can post events on Facebook wall
			 * Let's authorize him after he opens a detailed
			 * event view
			 */
			facebook.authorize(this, new DialogListener() {
				@Override
				public void onComplete(Bundle values) {
					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putString("access_token", facebook.getAccessToken());
					editor.putLong("access_expires", facebook.getAccessExpires());
					editor.commit();
				}

				@Override
				public void onFacebookError(FacebookError error) {}

				@Override
				public void onError(DialogError e) {}

				@Override
				public void onCancel() {}
			});
		}
	}

	// Callback for the feed dialog which updates the profile status
	public class UpdateStatusListener implements DialogListener {
		@Override
		public void onComplete(Bundle values) {
			final String postId = values.getString("post_id");
			if (postId != null) {
				Toast toast = Toast.makeText(getApplicationContext(), "Update Status executed",
						Toast.LENGTH_SHORT);
				toast.show();
			} else {
				Toast toast = Toast.makeText(getApplicationContext(), "No wall post made",
						Toast.LENGTH_SHORT);
				toast.show();
			}
		}

		@Override
		public void onFacebookError(FacebookError error) {
			Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(),
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel() {
			Toast toast = Toast.makeText(getApplicationContext(), "Update status cancelled",
					Toast.LENGTH_SHORT);
			toast.show();
		}

		@Override
		public void onError(DialogError e) {
			// TODO Auto-generated method stub
		}
	}
}
