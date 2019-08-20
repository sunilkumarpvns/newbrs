package com.elitecore.netvertex.gateway.file.util;



import java.io.File;
import java.io.IOException;


/**
 * A factory for creating EliteOutputStream objects.
 */
public class EliteOutputStreamFactory {

	private EliteOutputStreamFactory(){
	}
	/**
	 * Creates a new EliteOutputStream object.
	 *
	 * @param streamType the stream type
	 * @param fileName the file name
	 * @return the eliteoutputstreams
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final CrestelServiceOutputStream createStream(String streamType, File fileName) throws IOException{

		return new CrestelServiceOutputStream(fileName);
	}

}
