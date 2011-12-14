package com.retis.faitango;

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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class DataEventFetcherHTTP extends DataEventFetcher {

	/* Register the concrete Product's constructor to the Factory */
	static { DataEventFetcher.Factory.register("http", DataEventFetcherHTTP.class); }
	
	private Context appContext;
	private String uriScheme;
	private String uriHost;
	private String uriPath;
	private String uriQuery;
	private HttpClient httpClient;
	
	public DataEventFetcherHTTP(Context context) throws DataEventFetcherException {
		// chris TODO: do some initialization? read some configuration?
		appContext = context;
		
		uriScheme = "http";
		//chris FIXME: value HARDCODED, we have to read this from somewhere!
		uriPath = "/eventi/eventilistaJSON.asp"; 
		loadUriHost();
		
    	Log.d("chris", "Reading servename: '" + uriHost + "' len=" + uriHost.length() 
    			+ " context=" + appContext.toString());
    	if (uriHost.length() == 0)
    		throw new DataEventFetcherException("Remote HTTP server not specified"); 
	}
	
	@Override
	public String fetch(EventFilter filter) {
		// chris NOTE: we might implement the null policy (no filter)!
		
		createHttpClient();
		createQuery(filter);
		String response = performHttpGet();
		// chris TODO: do some check on this response?
		
		return response;
	}
	
	private String performHttpGet() {
		
		String responseMessage = null;
		BufferedReader in = null;
        HttpGet request = new HttpGet();
        try {
        	String uri = uriScheme + "://" + uriHost + uriPath + "?" + uriQuery;
        	Log.d("chris", "TESTIAMO LA URI: " + uri.toString());
			request.setURI(new URI(uri));
			Log.d("chris", "URI host is: " + request.getURI().getHost());
			Log.d("chris", "URI is: " + request.getURI().toASCIIString());
			HttpResponse response = httpClient.execute(request);			
			HttpEntity entity = response.getEntity();
            if (entity == null) {
                    Log.e("chris", "Empty Response");
                    return null; // chris TODO: Throw exception?
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	private void loadUriHost() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
    	uriHost =  prefs.getString("remoteHTTPServer", "");
	}
	
	private void createQuery(EventFilter f) {
		uriQuery  = "";
		if (f == null)			
			return;
		try {
		
		for (DataEvent.Types t : f.types)
			uriQuery += "cat_evento_" + t.enumId + "=1&";
		if (f.dateFrom != null) {
			String s = f.dateFrom.getDay() + "/" 
						+ f.dateFrom.getMonth() + "/" 
						+ f.dateFrom.getYear() ;
			uriQuery += "dt_dal=" + URLEncoder.encode(s,"UTF-8") + "&";
		}
		if (f.dateTo != null) {
			String s = f.dateTo.getDay() + "/" 
						+ f.dateTo.getMonth() + "/" 
						+ f.dateTo.getYear() ;
			uriQuery += "dt_al=" + URLEncoder.encode(s,"UTF-8") + "&";
		}
		if (f.country != null)
			uriQuery += "id_stati=" + "118" + "&"; // chris FIXME: REMOVE HARDCODING: 118 is the code for ITALY
		if (f.region != null)
			uriQuery += "regione=" + URLEncoder.encode(f.region,"UTF-8") + "&";
		if (f.area != null)
			uriQuery += "provincia=" + URLEncoder.encode(f.area,"UTF-8") + "&";
		if (f.title != null)
				uriQuery += "titolo=" + URLEncoder.encode(f.title,"UTF-8") + "&";		
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	private void createHttpClient() {
		httpClient = new DefaultHttpClient();
		// chris TODO: shall we use some more complex stuff? Like, creating the HTTP/HTTPS socket?
		//             SEE example below!
        
		//DefaultHttpClient ret = null;

        ////sets up parameters
        //HttpParams params = new BasicHttpParams();
        //HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        //HttpProtocolParams.setContentCharset(params, "utf-8");
        //params.setBooleanParameter("http.protocol.expect-continue", false);

        ////registers schemes for both http and https
        //SchemeRegistry registry = new SchemeRegistry();
        //registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        //final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
        //sslSocketFactory.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        //registry.register(new Scheme("https", sslSocketFactory, 443));

        //ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, registry);
        //ret = new DefaultHttpClient(manager, params);
        //return ret;
	}
}
