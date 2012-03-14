package com.retis.faitango.remote;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;


/** DataEvent parser base class <br><br>
 * 
 * This is a base class for DataEvent parsing.
 * 
 * Events should be written by the derived classes in the protected member {@link #events}.
 * The abstract methods {@link #parseEventList(String)} and {@link #parseEventDetail(String)} have to
 * be implemented in the derived classes to parse the brief and detailed information about the event.
 * 
 * @author Christian Nastasi
 */
public abstract class EventParser {

	/** Factory (inner) class to create DataEventParser objects <br><br>
	 * 
	 * We used a factory method pattern with static registration.
	 * The java reflection mechanism has been used to register the 
	 * Product's constructor. 
	 * Static initialization is exploited to register Product's constructors
	 * to the factory registry. 
	 */
	public static class Factory {

		/** Local Registry for Factory Method Pattern */
		private static final Map<String, Constructor<? extends EventParser>> registry = 
				new HashMap<String, Constructor<? extends EventParser>>();

		private static final String TAG = "DataEventParser.Factory";

		/** Factory registration method <br><br>
		 * 
		 * Registration method to be called by the concrete product classes to register 
		 * their constructor to the factory.
		 * 
		 * @param id			Identifier associated to the concrete product class
		 * @param fetcher		The constructor of a derived class of DataEventFetcher
		 * @throws Exception 
		 */
		public static void register(String id, Class<? extends EventParser> fetcher) throws Exception {
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
		 * @return			A DataEventParser object of the type associated with the identifier
		 * @throws Exception 
		 */
		public static EventParser create(String id, Context context) throws Exception {
			if (!registry.containsKey(id)) 
				throw new Exception("No class constructor registered for the given id = " + id);
			EventParser parser = registry.get(id).newInstance(context);
			return parser;
		}

		/* Force static initialization of all the concrete Product classes. 
		 * This will cause the 'static' statements of such classes to be called,
		 * and in these statements the registration of the concrete constructors 
		 * will be done. */
		static {
			try {
				Class.forName("com.retis.faitango.remote.JSONEventParser");
			} catch (ClassNotFoundException e) {
				Log.e(TAG, "ClassNotFoundException while executing the static initialization block that loads the concrete products:" +  e.getMessage());
				e.printStackTrace();
			}
		}
	}

	protected List<DataEvent> events = new ArrayList<DataEvent>();
	protected List<DataEventDetail> eventsDetails = new ArrayList<DataEventDetail>();
	protected Context appContext;

	protected EventParser(Context context) {
		appContext = context;
	}

	public List<DataEvent> getEvents() {
		return events;
	}
	
	public List<DataEventDetail> getEventsDetails() {
		return eventsDetails;
	}

	/** Get EventType from String <br><br>
	 * 
	 * The method attempt to returns an EventType matching the input description String according
	 * to the definition in the <code>string.xml</code> file.
	 * The input <code>s</code> in ignore-case compared against each even-type strings defined in the <code>string.xml</code>.
	 * If a match occurs the relative EventType is returned, otherwise null is returned.
	 *  
	 * @param s	Event-type string
	 * @return	The EventType matching the input string, otherwise null.
	 */
	protected EventType getEventTypeFromString(String s) {
		EventType allTypes[] = EventType.values();
		for (EventType t : allTypes) {
			if (appContext.getResources().getString(t.resId).equalsIgnoreCase(s))
				return t;
		}
		return null;
	}

	public abstract void parseEventList(String input);
	public abstract void parseEventDetail(String input);
}
