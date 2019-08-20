package com.elitecore.netvertex.gateway.file.parsing;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class AsciiParsingUnitInfo.
 */
public class AsciiParsingUnitInfo {

	/** The Constant HEADER_LIST. */
	public static final String HEADER_LIST = "HEADER_LIST";
	
	public static final String HEADER = "HEADER";

	/** The Constant HEADER_READ. */
	public static final String HEADER_READ = "HEADER_READ";

	/** The Constant FILE_NAME. */
	public static final String FILE_NAME = "FILE_NAME";

	/** The Constant FILE_PATH. */
	public static final String FILE_PATH = "FILE_PATH";

	/** The Constant PLUGIN_ID. */
	public static final String PLUGIN_ID = "PLUGIN_ID";
	
	private Map<String,Object> objectValueMap;

	private int totalRecords;

	/**
	 * Instantiates a new ascii parsing unit info.
	 *
	 * @param fileName the file name
	 * @param filePath the file path
	 * @param pluginId the plugin id
	 */
	protected AsciiParsingUnitInfo(String fileName, String filePath, String pluginId){
		objectValueMap = new HashMap<>();
		objectValueMap.put(FILE_NAME, fileName);
		objectValueMap.put(FILE_PATH, filePath);
		objectValueMap.put(PLUGIN_ID, pluginId);
		totalRecords = 0;
	}

	/**
	 * Sets the object value.
	 *
	 * @param key the key
	 * @param value the value
	 */
	protected void setObjectValue(String key, Object value){
		if(key != null && value != null)
			objectValueMap.put(key, value);
	}

	/**
	 * Increment total records count.
	 */
	protected void incrementTotalRecordsCount() {
		totalRecords++;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.plugins.parsing.IParsingUnitInfo#getTotalRecords()
	 */
	public int getTotalRecords() {
		return totalRecords;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.plugins.parsing.IParsingUnitInfo#getObjectValue(java.lang.String)
	 */
	public Object getObjectValue(String key) {
		if(key != null)
			return objectValueMap.get(key);
		return null;
	}
}
