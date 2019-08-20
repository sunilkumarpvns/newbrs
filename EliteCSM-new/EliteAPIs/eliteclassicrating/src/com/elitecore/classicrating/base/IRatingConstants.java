/**
 *	Elite Classic Rating Project
 *	Created on Sep 26, 2008
 *	Created by  Raghu.G
 */

/*
 *  IEliteRatingConstants.java
 *  This interface is used to declare all commonly used constants
 */

package com.elitecore.classicrating.base;


public interface IRatingConstants {

	// home folder of rating
	public final String RATING_HOME = System.getenv("RATING_HOME") ;
	
	// xml configuration naming constants
	
	public static final String LOG_FOLDER	     	= "log-folder";
	public static final String LOG_FILE_NAME    	= "log-file-name";		
	public static final String LOG_LEVEL	     	= "log-level";

	public static final String DATASOURCE_NAME     = "datasource-name";
	public static final String CONNECTION_URL      = "connection-url";
	public static final String USER_NAME	       = "user-name";
	public static final String PASSWORD    		   = "password";	
	public static final String MIN_POOL_SIZE       = "min-pool-size";
	public static final String MAX_POOL_SIZE	   = "max-pool-size";
	public static final String CACHE_NAME    	   = "cache-name";	
	public static final String ALLOCATE_IF_EXCEED  = "allocate-if-exceed";		

	public static final String DEFAULT_MIN_CONNECTION_LIMIT = "3";
	public static final String DEFAULT_MAX_CONNECTION_LIMIT = "6";	

	public static final int OFF 				= 0;
	public static final int INFO 				= 1;
	public static final int WARN 				= 2;
	public static final int DEBUG 				= 3;
	public static final int ERROR 				= 4;
	public static final int FATAL 				= 5;
	public static final int TRACE 				= 7;
	public static final int ALL 				= 8;

}
