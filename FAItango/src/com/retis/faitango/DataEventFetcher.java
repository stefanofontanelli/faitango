package com.retis.faitango;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;

public abstract class DataEventFetcher {

	/* Factory (inner) class to create DataEventFetcher objects
	 * 
	 * We used a factory method pattern with static registration.
	 * The java reflection mechanism has been used to register the 
	 * Product's constructor. 
	 * Static initialization is exploited to register Product's constructors
	 * to the factory registry (which is an hash map). 
	 *  */
	public static class Factory {

		/* Local Registry for Factory Method Pattern */
		static final private HashMap<String, Constructor<? extends DataEventFetcher>> registry = 
				new HashMap<String, Constructor<? extends DataEventFetcher>>();
		
		/* Registration function, to be called by the Product classes */
		static public void register(String id, Class<? extends DataEventFetcher> fetcher) {
			if (registry.containsKey(id)) 
				return;//chris TODO: generate error: exception? return boolean?
			try {
				registry.put(id, fetcher.getConstructor(Context.class));
			//} catch (Exception e) {}
			///*chris TODO: handle specific exceptions?
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//*/
		}
		
		/* Factory creator methods: access the registry and uses the proper
		 * registered constructor. */
		static public DataEventFetcher create(String id, Context context) {
			if (!registry.containsKey(id)) 
				return null;//chris TODO: generate error: exception? return boolean?
			DataEventFetcher fetcher = null;
			try {
				fetcher = registry.get(id).newInstance(context);
			} catch (Exception e) {
				Log.e("chris", "CREATING FETCHER: ");
				e.printStackTrace();
				return null;
			}
			/* 
			 * chris TODO: handle specific exceptions?
			 *
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			*/
			return fetcher;
		}
		
		/* Force static initialization of all the concrete Product classes. 
		 * This will cause the 'static' statements of such classes to be called,
		 * and in these statements the registration of the concrete constructors 
		 * will be done. */
		static {
			try {
				Class.forName("com.retis.faitango.DataEventFetcherHTTP");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	protected Context appContext;
	
	protected DataEventFetcher(Context context) {
		appContext = context;
	}
	
	abstract public String fetchEventList(EventFilter filter);
	abstract public String fetchEventDetail(long id);
}
