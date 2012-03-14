package com.retis.faitango.remote;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;


import android.content.Context;
import android.util.Log;

public abstract class DataEventFetcher {

	/** Factory (inner) class to create DataEventFetcher objects <br><br>
	 * 
	 * We used a factory method pattern with static registration.
	 * The java reflection mechanism has been used to register the 
	 * Product's constructor. 
	 * Static initialization is exploited to register Product's constructors
	 * to the factory registry. 
	 */
	public static class Factory {

		/** Local Registry for Factory Method Pattern */
		private static final Map<String, Constructor<? extends DataEventFetcher>> registry = 
				new HashMap<String, Constructor<? extends DataEventFetcher>>();

		private static final String TAG = "DataEventFetcher.Factory";

		/** Factory registration method <br><br>
		 * 
		 * Registration method to be called by the concrete product classes to register 
		 * their constructor to the factory.
		 * 
		 * @param id			Identifier associated to the concrete product class
		 * @param fetcher		The constructor of a derived class of DataEventFetcher
		 * @throws Exception 
		 */
		public static void register(String id, Class<? extends DataEventFetcher> fetcher) throws Exception {
			if (registry.containsKey(id)) 
				throw new Exception("Class constructor already registered in the factory registry with name = " + registry.get(id).getName());
			registry.put(id, fetcher.getConstructor(Context.class));
		}

		/** Factory creator <br><br>
		 * 
		 * Method of the abstract factory to create abstract products. 
		 * This access the registry and uses the proper registered constructor to create the concrete class.
		 * 
		 * @param id		Identifier associated to the concrete product class
		 * @param context	Context argument to be passed to the product constructor
		 * @return			A DataEventFetcher object of the type associated with the identifier
		 * @throws Exception 
		 */
		public static DataEventFetcher create(String id, Context context) throws Exception {
			if (!registry.containsKey(id)) 
				throw new Exception("No class constructor registered for the given id = " + id);
			DataEventFetcher fetcher = registry.get(id).newInstance(context);
			return fetcher;
		}

		/* Force static initialization of all the concrete Product classes. 
		 * This will cause the 'static' statements of such classes to be called,
		 * and in these statements the registration of the concrete constructors 
		 * will be done. */
		static {
			try {
				Class.forName("com.retis.faitango.remote.DataEventFetcherHTTP");
			} catch (ClassNotFoundException e) {
				Log.e(TAG, "ClassNotFoundException while executing the static initialization block that loads the concrete products:" +  e.getMessage());
				e.printStackTrace();
			}
		}
	}

	protected Context appContext;

	protected DataEventFetcher(Context context) {
		appContext = context;
	}

	public abstract String fetchEventList(EventFilter filter);
	public abstract String fetchEventDetail(long id);
}