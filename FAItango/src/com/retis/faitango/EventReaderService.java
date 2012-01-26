package com.retis.faitango;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/** Service-wrapper for EventReader<br><br>
 *
 * The service wraps the {@link EventReader} operations. 
 * This is meant to be executed for background automatic synchronization.
 * The service should be started through the {@link EventReaderAlarm} class. 
 * <br>
 * The {@link EventFilter} object (parcelable), used to set an event fetch filter, 
 * is encoded as an extras in the activation Intent.     
 * 
 * @author Christian Nastasi
 */
public class EventReaderService extends Service {

	private Intent intent;
	private static final String TAG = "EventReaderService";
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent i, int flags, int startId) {
		intent = i;
		new Thread(new Runnable() {
			public void run() {
				try {
					EventReader reader = new EventReader(EventReaderService.this);
					// Execute actual service code
					Log.d(TAG, "Exetuting EventReader from SERVICE");
					reader.execute((EventFilter) intent.getParcelableExtra("EventFilter"));
				} catch (Exception e) {
					Log.e(TAG, "onStartCommand(): EventReader exception: " + e.getMessage());
					// TODO: shall we do some error notification (in the GUI)?
				}
				stopSelf();
			}
		}).start();
		//stopSelf();
		return START_STICKY;
	}
}
