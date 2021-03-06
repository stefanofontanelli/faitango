package com.retis.faitango.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.retis.faitango.R;
import com.retis.faitango.preference.PreferenceHelper;

import android.content.Context;
import android.util.Log;

public class DataEventFetcherHTTP extends DataEventFetcher {

	/* Register the concrete Product's constructor to the Factory */
	static {
		try {
			DataEventFetcher.Factory.register("http", DataEventFetcherHTTP.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}

	private static final String uriScheme = "http";
	private String uriHost;
	private final String uriPathEventList;
	private final String uriPathEventDetail;
	private String uriQueryEventList;
	private String uriQueryEventDetail;
	private HttpClient httpClient;
	private static final String TAG = "DataEventFetcherHTTP";
	private HttpGet request;

	public DataEventFetcherHTTP(Context context) throws Exception {
		super(context); // Initializes appContext

		uriPathEventList = context.getResources().getString(R.string.httpUriPathEventList);
		uriPathEventDetail = context.getResources().getString(R.string.httpUriPathEventDetail);
		uriHost = PreferenceHelper.getRemoteServer(context);
		request = new HttpGet();
		if (uriHost.length() == 0)
			throw new Exception(TAG + ": Remote HTTP server not specified"); 
	}

	@Override
	public String fetchEventList(EventFilter filter) {
		createHttpClient();
		createQueryEventList(filter);
		String response = performHttpGet(uriPathEventList, uriQueryEventList);
		Log.d(TAG, "Fetch Event List. Response: " + response);
		return response;
	}

	@Override
	public String fetchEventDetail(long eventId) {
		createHttpClient();
		createQueryEventDetail(eventId);
		String response = performHttpGet(uriPathEventDetail, uriQueryEventDetail);
		return response;
	}

	@Override
	public void stopEventFetch() {
		request.abort();
	}

	private String performHttpGet(String uriPath, String uriQuery) {

		String responseMessage = null;
		BufferedReader in = null;

		try {
			String uri = uriScheme + "://" + uriHost + uriPath + "?" + uriQuery;
			request.setURI(new URI(uri));
			Log.d(TAG, "Fetching URI is: " + request.getURI().toASCIIString());
			HttpResponse response = httpClient.execute(request);			
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				Log.e(TAG, "Empty Response");
				return null;
			}
			in = new BufferedReader(new InputStreamReader(entity.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			responseMessage = sb.toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			Log.w(TAG, "Receiving IOException: Maybe an abort() on HttpGet has occurred");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return responseMessage;
	}

	private void createQueryEventList(EventFilter f) {
		uriQueryEventList  = "";
		if (f == null)			
			return;
		try {

			for (EventType t : f.types)
				uriQueryEventList += "cat_evento_" + t.enumId + "=1&";
			if (f.dateFrom != null) {
				String s = f.dateFrom.getDate() + "/" 
						+ (f.dateFrom.getMonth() + 1) + "/" 
						+ (f.dateFrom.getYear() + 1900);
				uriQueryEventList += "dt_dal=" + URLEncoder.encode(s,"UTF-8") + "&";
			}
			if (f.dateTo != null) {
				String s = f.dateTo.getDate() + "/" 
						+ (f.dateTo.getMonth() + 1) + "/" 
						+ (f.dateTo.getYear() + 1900);
				uriQueryEventList += "dt_al=" + URLEncoder.encode(s,"UTF-8") + "&";
			}
			if (f.country != null)
				uriQueryEventList += "id_stati=" + f.country + "&";
			if (f.region != null)
				uriQueryEventList += "regione=" + URLEncoder.encode(f.region,"UTF-8") + "&";
			if (f.province != null)
				uriQueryEventList += "provincia=" + URLEncoder.encode(f.province,"UTF-8") + "&";
			if (f.title != null)
				uriQueryEventList += "titolo=" + URLEncoder.encode(f.title,"UTF-8") + "&";		
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
	}

	private void createQueryEventDetail(long eventId) {
		uriQueryEventDetail = "idevento=" + eventId;
	}

	private void createHttpClient() {
		httpClient = new DefaultHttpClient();
	}
}
