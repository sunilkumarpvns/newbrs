/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 15, 2008
 *	@author Hardik Shah
 *
 */
package com.elitecore.classicrating.api;

import java.io.Serializable;

/**
 * @author hardikshah
 *
 */
public interface IRequestParameters extends Serializable {
	
	public String get(String key) ;
	
	public String put(String key ,String value) ;
	
}
