/*
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on Oct 1, 2008
 *	@author baiju
 *  Last Modified Oct 1, 2008
 */

/*
 * IRatingAppContext.java
 * 
 * This interface is used to get an instance of IDataSourceProvider
 * 
 */

package com.elitecore.classicrating.base;

import com.elitecore.classicrating.datasource.IDataSourceProvider;


public interface IRatingAppContext {

	public IDataSourceProvider getDataSourceProvider();

}
