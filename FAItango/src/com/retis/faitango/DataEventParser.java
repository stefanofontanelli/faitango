package com.retis.faitango;

// Base class for the parser of the social event data
public abstract class DataEventParser {

	// chris NOTE:Fields taken from the JSON file they gave us
	protected String id;
	protected String text;
	protected String date;
	protected String city;
	protected String type;
	protected String af; // chris TODO: what is this? give a better name

	public DataEventParser (/*something?*/) {
	}

	// Getter methods
	public String getId()   {return id;} 
	public String getText() {return text;}
	public String getDate() {return date;}
	public String getCity() {return city;}
	public String getType() {return type;}
	public String getAf()   {return af;} // chris TODO: see 'af' member

	// Abstract method to be implemented in by concrete parsers
	abstract void parse();
}