package com.retis.faitango;

public class DataEventFetcherHTTP extends DataEventFetcher {

	/* Register the concrete Product's constructor to the Factory */
	static { DataEventFetcher.Factory.register("http", DataEventFetcherHTTP.class); }
	
	public DataEventFetcherHTTP() {
		// chris TODO: do some initialization? read some configuration?
	}
	
	@Override
	public String fetch() { 
		String jsonTest = "[{\"id\":89174,\"tx\":\"Milonga Linda Sp\u00E9cial Anniversaire\",\"dt\":" +
                "\"mar 06\\/12\\/2011\",\"citta\":\"Francia - Cagnes sur Mer\",\"type\"" + 
		            ":\"Milonga\",\"af\":null}]";
		return jsonTest;
	}
}
