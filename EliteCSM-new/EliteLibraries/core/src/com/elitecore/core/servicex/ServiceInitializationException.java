/*
 *  Server Framework
 *  
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *  
 *  Created on 7th April 2006
 *  
 */


package com.elitecore.core.servicex;

import com.elitecore.core.commons.util.constants.ServiceRemarks;


public class ServiceInitializationException extends Exception {

	private static final long serialVersionUID = 1L;

	private ServiceRemarks remark;

	public ServiceInitializationException(String message, ServiceRemarks remark) {
		super(message);    
		this.remark = remark;
	}

	public ServiceInitializationException(String message, ServiceRemarks remark, Throwable cause) {
		super(message, cause);    
		this.remark = remark;
	}

	public ServiceInitializationException(ServiceRemarks remark, Throwable cause) {
		super(cause);    
		this.remark = remark;
	}

	public ServiceRemarks getRemark() {
		return remark;
	}

}
