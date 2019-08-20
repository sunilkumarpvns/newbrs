package com.elitecore.commons.io;

import static com.elitecore.commons.base.Preconditions.checkArgument;
import static com.elitecore.commons.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A set of utilities related to File operations.
 * 
 * @author narendra.pathai
 *
 */
public class Files {

	/**
	 * Read full bytes of the given file.
	 * 
	 * @param file a non-null file whose bytes will be read
	 * @return full bytes of the given file
	 * @throws FileNotFoundException if file does not exist
	 * @throws IOException if some issue occurs while reading from file
	 * @throws IllegalArgumentException if file is a directory
	 */
	public static byte[] readFully(File file) throws FileNotFoundException, IOException, IllegalArgumentException {
		checkNotNull(file, "file is null");
		checkArgument(file.isFile(), file.getPath() + " is not a file");
		
		return ByteStreams.readFully(new FileInputStream(file));
	}

	/**
	 * Read full bytes of the file at given path.
	 * 
	 * @param filePath non-null path of the file whose bytes will be read
	 * @return full bytes of file at given path
	 * @throws FileNotFoundException if file at given path does not exist 
	 * @throws IOException if some issue occurs while reading from file
	 * @throws IllegalArgumentException if filePath points to a directory 
	 */
	public static byte[] readFully(String filePath) throws FileNotFoundException, IOException, IllegalArgumentException {
		checkNotNull(filePath, "filePath is null");
		
		return readFully(new File(filePath));
	}
}
