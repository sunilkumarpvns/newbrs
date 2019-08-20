package com.elitecore.core.util.csv;

/**
 * The class InvalidCSVFileException is a form of Throwable that indicates
 * either file is null or directory or not a csv file. 
 * @author Manjil Purohit
 *
 */
public class InvalidCSVFileException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public InvalidCSVFileException() {
		super("Invalid CSV File !");
	}
	
	public InvalidCSVFileException(String msg) {
		super(msg);
	}
	
	public InvalidCSVFileException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public InvalidCSVFileException(Throwable cause) {
		super(cause);
	}
	
}
