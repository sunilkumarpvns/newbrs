/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 1, 2008
 *	@author Raghu G
 *      Last Modified October 1, 2008
 */

/*
 * EliteClassicRatingAppContext.java
 * This class provides implementation of DefaultRatingAppContext to provide
 * an instance of type IDataSourceProvider.
 * 
 */

package com.elitecore.classicrating.datasource;

import com.elitecore.classicrating.base.DefaultRatingAppContext;

public class EliteClassicRatingAppContext extends  DefaultRatingAppContext{
	
	IDataSourceProvider dataSourceProvider;
	
	public EliteClassicRatingAppContext(IDataSourceProvider dataSourceProvider){
		this.dataSourceProvider=dataSourceProvider;
	}
	
	public IDataSourceProvider getDataSourceProvider(){
		return dataSourceProvider;
	}
	
}