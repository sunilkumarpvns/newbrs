/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on Oct 1, 2008
 *	@author baiju
 *      Last Modified Oct 1, 2008
 */

/*
 * IDataSourceProvider.java
 * 
 * This interface declares a method that returns an instance of type IRatingDataSource. 
 * 
 */

package com.elitecore.classicrating.datasource;

import java.sql.SQLException;

public interface IDataSourceProvider {

	IRatingDataSource getRatingDataSource() throws SQLException;

}
