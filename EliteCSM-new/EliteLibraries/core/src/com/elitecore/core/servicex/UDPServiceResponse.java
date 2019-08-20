package com.elitecore.core.servicex;

public interface UDPServiceResponse extends ServiceResponse {

	/**
	 * 
	 * @return Returns the bytes to be sent back in response.
	 */
	public byte[] getResponseBytes();

}
