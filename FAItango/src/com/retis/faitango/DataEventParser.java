package com.retis.faitango;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

import com.retis.faitango.DataEvent;

// Base class for the parser of the social event data
public abstract class DataEventParser {

	/* Factory (inner) class to create DataEventParser objects
	 * 
	 * We used a factory method pattern with static registration.
	 * The java reflection mechanism has been used to register the 
	 * Product's constructor. 
	 * Static initialization is exploited to register Product's constructors
	 * to the factory registry (which is an hash map). 
	 *  */
	public static class Factory {

		/* Local Registry for Factory Method Pattern */
		static final private HashMap<String, Constructor<? extends DataEventParser>> registry = 
				new HashMap<String, Constructor<? extends DataEventParser>>();
		
		/* Registration function, to be called by the Product classes */
		static public void register(String id, Class<? extends DataEventParser> parser) {
			if (registry.containsKey(id)) 
				return;//chris TODO: generate error: exception? return boolean?
			try {
				registry.put(id, parser.getConstructor());
			} catch (Exception e) {}
			/* 
			 * chris TODO: handle specific exceptions?
			 *
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
		}
		
		/* Factory creator methods: access the registry and uses the proper
		 * registered constructor. */
		static public DataEventParser create(String id) {
			if (!registry.containsKey(id)) 
				return null;//chris TODO: generate error: exception? return boolean?
			DataEventParser parser = null;
			try {
				parser = registry.get(id).newInstance();
			} catch (Exception e) {
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
			return parser;
		}
		
		/* Force static initialization of all the concrete Product classes. 
		 * This will cause the 'static' statements of such classes to be called,
		 * and in these statements the registration of the concrete constructors 
		 * will be done. */
		static {
			try {
				Class.forName("com.retis.faitango.DataEventParserJSON");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	protected ArrayList<DataEvent> events = new ArrayList<DataEvent>();
	
	public ArrayList<DataEvent> getEvents() {return events;}

	// Abstract method to be implemented in by concrete parsers
	abstract public void parse(String input);

	abstract String getTestString();
}
