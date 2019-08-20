/*
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on Oct 1, 2008
 *	@author baiju
 *      Last Modified Oct 1, 2008
 */

/*
 * IRatingDataSource.java
 * 
 * This interface declares a method that returns an instance of type IRatingDBConnection. 
 * 
 */
package com.elitecore.classicrating.datasource;

import java.sql.SQLException;

public interface IRatingDataSource {

	public IRatingDBConnection getConnection() throws SQLException;

	public void close(IRatingDBConnection connection) throws SQLException;

}
