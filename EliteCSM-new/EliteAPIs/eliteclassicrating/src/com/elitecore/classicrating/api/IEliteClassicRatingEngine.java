 /**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 15, 2008
 *	@author Hardik Shah
 *
 */
package com.elitecore.classicrating.api;

/**
 * @author hardikshah
 *
 */
public interface IEliteClassicRatingEngine {
	
	/**
	 * 
	 * @param requestParameters
	 * @return
	 */
	public IResponseObject authorizationRequest(IRequestParameters requestParameters);
	
	/**
	 * 
	 * @param requestParameters
	 * @return
	 */
	public IResponseObject accountingRequest(IRequestParameters requestParameters);

}
